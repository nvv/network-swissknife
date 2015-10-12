package com.nsak.android.network.utils;

import android.os.AsyncTask;

import com.nsak.android.App;
import com.nsak.android.utils.CommandLineUtils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
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

    public static Observable<CommandLineUtils.CommandLineCommandOutput> pingCommand(String ip) {
        return CommandLineUtils.executeCommand("ping", ip).skip(1);
    }

    public static Observable<CommandLineUtils.CommandLineCommandOutput> tracerouteCommand(final String host){
        File traceroute = new File(App.sInstance.getApplicationInfo().dataDir, "traceroute");
        if (!traceroute.exists()) {
            try {
                copyFdToFile(App.sInstance.getAssets().open("traceroute"), traceroute);
                Runtime.getRuntime().exec("chmod 770 " + traceroute.getAbsolutePath()).waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return CommandLineUtils.executeCommand(traceroute.getAbsolutePath(), host).
                subscribeOn(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR)).
                map(new Func1<CommandLineUtils.CommandLineCommandOutput, CommandLineUtils.CommandLineCommandOutput>() {
                    @Override
                    public CommandLineUtils.CommandLineCommandOutput call(CommandLineUtils.CommandLineCommandOutput commandLineCommandOutput) {
                        // before exec
                        if (commandLineCommandOutput.process == null) {
                            try {
                                commandLineCommandOutput.args[1] = InetAddress.getByName(commandLineCommandOutput.args[1]).getHostAddress();
                            } catch (Exception ignore) {}
                        }

                        return commandLineCommandOutput;
                    }
                }).skip(1);
    }

    public static Observable<String> whoisCommand(final String host) {
        return webExctractCommand("https://www.markmonitor.com/cgi-bin/affsearch.cgi?dn=" + host, "PRE");
    }

    public static Observable<String> whatIsMyIpCommand() {
        return webExctractCommand("http://checkip.dyndns.com", "body").map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return s.split(":")[1];
            }
        });
    }

    public static Observable<Map<String, String>> getIspCommand() {
        return whatIsMyIpCommand().map(new Func1<String, Map<String, String>>() {
            @Override
            public Map<String, String> call(String ip) {
                Map<String, String> ispInfo = new HashMap<>();

                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpResponse response = client.execute(new HttpGet(String.format("http://ipinfo.io/%s/json", ip.trim())));
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        JSONObject object = new JSONObject(out.toString());

                        Iterator<String> keys = object.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            ispInfo.put(key, object.getString(key));
                        }

                        out.close();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return ispInfo;
                }
                return ispInfo;
            }
        }).subscribeOn(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR));
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
