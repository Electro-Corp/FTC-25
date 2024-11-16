package org.firstinspires.ftc.teamcode.jbbfi;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;
import com.qualcomm.robotcore.util.WebHandlerManager;
import com.qualcomm.robotcore.util.WebServer;

import org.firstinspires.ftc.ftccommon.external.OnCreate;
import org.firstinspires.ftc.ftccommon.external.WebHandlerRegistrar;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.robotcore.internal.webserver.WebHandler;
import org.firstinspires.ftc.robotserver.internal.webserver.MimeTypesUtil;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

public class ScriptWebPortal {
    private static boolean suppressOpMode = false;
    private static ScriptWebPortal instance;

    public static void suppressOpMode() {
        suppressOpMode = true;
    }

    @OpModeRegistrar
    public static void registerOpMode(OpModeManager manager) {
        if (instance != null && !suppressOpMode) {
            instance.internalRegisterOpMode(manager);
        }
    }

    private void internalRegisterOpMode(OpModeManager manager) {
    }

    @OnCreate
    public static void start(Context context) {
        if (instance == null) {
            instance = new ScriptWebPortal();
        }
    }

    @WebHandlerRegistrar
    public static void attachWebServer(Context context, WebHandlerManager manager) {
        if (instance != null) {
            instance.internalAttachWebServer(manager.getWebServer());
        }
    }

    private void internalAttachWebServer(WebServer webServer) {
        if (webServer == null) return;

        Activity activity = AppUtil.getInstance().getActivity();

        if (activity == null) return;

        WebHandlerManager webHandlerManager = webServer.getWebHandlerManager();
        AssetManager assetManager = activity.getAssets();
        webHandlerManager.register("/sdcard/scripting",
                newStaticAssetHandler(assetManager, "/sdcard/scripting/index.html"));
        webHandlerManager.register("/sdcard/scripting/",
                newStaticAssetHandler(assetManager, "/sdcard/scripting/index.html"));
    }

    private WebHandler newStaticAssetHandler(final AssetManager assetManager, final String file) {
        return new WebHandler() {
            @Override
            public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session)
                    throws IOException {
                if (session.getMethod() == NanoHTTPD.Method.GET) {
                    String mimeType = MimeTypesUtil.determineMimeType(file);
                    return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK,
                            mimeType, assetManager.open(file));
                } else {
                    return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND,
                            NanoHTTPD.MIME_PLAINTEXT, "");
                }
            }
        };
    }
}
