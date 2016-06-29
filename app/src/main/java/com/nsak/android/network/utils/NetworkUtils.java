package com.nsak.android.network.utils;

import android.os.AsyncTask;

import com.nsak.android.App;
import com.nsak.android.network.data.IspData;
import com.nsak.android.network.data.PingData;
import com.nsak.android.network.data.TracerouteData;
import com.nsak.android.utils.CommandLineUtils;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rx.Notification;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * @author Vlad Namashko.
 */
public class NetworkUtils {

    public static boolean ping(String host) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("ping", "-c", "1", host);
        Process proc = processBuilder.start();

        int returnVal = proc.waitFor();
        return returnVal == 0;
    }

    public static Observable<CommandLineUtils.CommandLineCommandOutput> pingCommand(String ip, String ... args) {
        String[] compoundArgs = new String[2 + args.length];
        compoundArgs[0] = "ping";
        System.arraycopy(args, 0, compoundArgs, 1, args.length);
        compoundArgs[compoundArgs.length - 1] = ip;

        return CommandLineUtils.executeCommand(compoundArgs).
                subscribeOn(Schedulers.computation()).
                skip(2).
                map(new Func1<CommandLineUtils.CommandLineCommandOutput, CommandLineUtils.CommandLineCommandOutput>() {
                    @Override
                    public CommandLineUtils.CommandLineCommandOutput call(CommandLineUtils.CommandLineCommandOutput output) {
                        if (output.outputNum > 0) {
                            output.mData = new PingData(output.outputLine);
                        }
                        return output;
                    }
                });
    }

    public static Observable<CommandLineUtils.CommandLineCommandOutput> tracerouteCommand(final String host) {
        final File traceroute = new File(App.sInstance.getApplicationInfo().dataDir, "traceroute");
        if (!traceroute.exists()) {
            try {
                copyFdToFile(App.sInstance.getAssets().open("traceroute"), traceroute);
                Runtime.getRuntime().exec("chmod 770 " + traceroute.getAbsolutePath()).waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                    try {
                        subscriber.onNext(InetAddress.getByName(host).getHostAddress());
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                subscriber.onCompleted();
            }

        }).flatMap(new Func1<String, Observable<CommandLineUtils.CommandLineCommandOutput>>() {
            @Override
            public Observable<CommandLineUtils.CommandLineCommandOutput> call(String s) {
                return CommandLineUtils.executeCommand(traceroute.getAbsolutePath(), s);
            }
        }).subscribeOn(Schedulers.computation())
                .skip(2)
                .map(new Func1<CommandLineUtils.CommandLineCommandOutput, CommandLineUtils.CommandLineCommandOutput>() {

            @Override
            public CommandLineUtils.CommandLineCommandOutput call(CommandLineUtils.CommandLineCommandOutput output) {
                output.mData = new TracerouteData(output.outputLine);
                return output;
            }
        });
    }

    public static Observable<CommandLineUtils.CommandLineCommandOutput> whoisCommand(final String host) {
        return webExctractCommand("https://www.markmonitor.com/cgi-bin/affsearch.cgi?dn=" + host, "PRE").
                map(new Func1<String, CommandLineUtils.CommandLineCommandOutput>() {
                    @Override
                    public CommandLineUtils.CommandLineCommandOutput call(String s) {
                        CommandLineUtils.CommandLineCommandOutput output = new CommandLineUtils.CommandLineCommandOutput();
                        output.args = new String[] { host };
                        output.outputLine = s;
                        output.outputNum = 0;
                        return output;
                    }
                });
    }

    public static Observable<String> whatIsMyIpCommand() {
        return webExctractCommand("http://checkip.dyndns.com", "body").map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return s.split(":")[1];
            }
        });
    }

    public static Observable<CommandLineUtils.CommandLineCommandOutput> getIspCommand() {
        return whatIsMyIpCommand().flatMap(new Func1<String, Observable<String>>() {
            @Override
            public Observable<String> call(final String ip) {
                return Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        HttpURLConnection urlConnection = null;
                        try {
                            URL url = new URL(String.format("http://ipinfo.io/%s/json", ip.trim()));
                            urlConnection = (HttpURLConnection) url.openConnection();
                            InputStream in = urlConnection.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                            String line;

                            StringBuilder data = new StringBuilder();
                            while ((line = reader.readLine()) != null) {
                                data.append(line);
                            }

                            subscriber.onNext(data.toString());
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                            e.printStackTrace();
                        } finally {
                            if (urlConnection != null) {
                                urlConnection.disconnect();
                            }
                        }
                    }
                });
            }
        }).subscribeOn(Schedulers.io()).flatMap(new Func1<String, Observable<CommandLineUtils.CommandLineCommandOutput>>() {
            @Override
            public Observable<CommandLineUtils.CommandLineCommandOutput> call(final String json) {
                return Observable.create(new Observable.OnSubscribe<CommandLineUtils.CommandLineCommandOutput>() {
                    @Override
                    public void call(Subscriber<? super CommandLineUtils.CommandLineCommandOutput> subscriber) {

                        try {
                            JSONObject object = new JSONObject(json);

                            Iterator<String> keys = object.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                String value = (String) object.get(key);
                                CommandLineUtils.CommandLineCommandOutput out = new CommandLineUtils.CommandLineCommandOutput();
                                out.outputLine = key + " = " + value;
                                out.mData = new IspData(key, value);
                                subscriber.onNext(out);
                            }
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                });
            }
        });
    }

    private static Observable<String> webExctractCommand(final String webResource, final String tagToExtract) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    Document doc = Jsoup.connect(webResource).get();
                    Elements record = doc.getElementsByTag(tagToExtract);

                    subscriber.onNext(record.text());
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }

            }
        }).subscribeOn(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR));
    }

    private static void copyFdToFile(InputStream src, File dst) throws IOException {

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(dst);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = src.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } finally {
            try {
                out.close();
            } catch (Exception ignore) {}
        }
    }


    public static boolean udpScan(String host, int port, int timeOut) {
        Socket socket = null;
        try {
            socket = new Socket();
            socket.bind(null);
            socket.connect(new InetSocketAddress(host, port), timeOut);
            return true;
        } catch (Exception ignored) {
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception ignored) {
            }
        }

        return false;
    }
}
