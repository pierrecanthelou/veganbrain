package app.veganbrain.banc;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;

/**
 * Banc jetable du spike Maestro (tâche 10) : une demande d'autorisation de notifier, une notification
 * qui rouvre l'app, une demande d'autorisation caméra suivie d'un aperçu. Chaque résultat s'affiche en texte,
 * pour que Maestro puisse le vérifier.
 */
public class BancActivity extends Activity {
    private static final int DEMANDE_NOTIF = 1;
    private static final int DEMANDE_CAMERA = 2;
    private static final String CANAL = "rappels";
    private static final String EXTRA_ORIGINE = "origine";

    private TextView etat;
    private TextView images;
    private TextureView apercu;
    private CameraDevice camera;
    private int nbImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout racine = new LinearLayout(this);
        racine.setOrientation(LinearLayout.VERTICAL);
        racine.setPadding(32, 160, 32, 32);

        etat = new TextView(this);
        etat.setTextSize(18);
        racine.addView(etat);
        images = new TextView(this);
        racine.addView(images);

        racine.addView(bouton("Demander notifications", () ->
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, DEMANDE_NOTIF)));
        racine.addView(bouton("Poster notification", this::posterNotification));
        racine.addView(bouton("Ouvrir caméra", this::ouvrirCamera));

        apercu = new TextureView(this);
        racine.addView(apercu, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 900));
        setContentView(racine);

        etat.setText("Prêt");
        lireOrigine(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        lireOrigine(intent);
    }

    private void lireOrigine(Intent intent) {
        if ("notification".equals(intent.getStringExtra(EXTRA_ORIGINE))) {
            etat.setText("Ouvert depuis la notification");
        }
    }

    private Button bouton(String texte, Runnable action) {
        Button b = new Button(this);
        b.setText(texte);
        b.setOnClickListener(v -> action.run());
        return b;
    }

    private boolean accordee(String permission) {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int code, String[] permissions, int[] resultats) {
        boolean ok = resultats.length > 0 && resultats[0] == PackageManager.PERMISSION_GRANTED;
        if (code == DEMANDE_NOTIF) {
            etat.setText(ok ? "Notifications accordées" : "Notifications refusées");
        } else if (code == DEMANDE_CAMERA) {
            if (ok) demarrerApercu(); else etat.setText("Caméra refusée");
        }
    }

    private void posterNotification() {
        if (!accordee(Manifest.permission.POST_NOTIFICATIONS)) {
            etat.setText("Notifications non accordées");
            return;
        }
        NotificationManager nm = getSystemService(NotificationManager.class);
        nm.createNotificationChannel(new NotificationChannel(CANAL, "Rappels", NotificationManager.IMPORTANCE_HIGH));
        Intent ouvrir = new Intent(this, BancActivity.class).putExtra(EXTRA_ORIGINE, "notification")
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, ouvrir,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        Notification n = new Notification.Builder(this, CANAL)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Rappel du banc")
                .setContentText("Noter le produit")
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();
        nm.notify(1, n);
        etat.setText("Notification postée");
    }

    private void ouvrirCamera() {
        if (accordee(Manifest.permission.CAMERA)) {
            demarrerApercu();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, DEMANDE_CAMERA);
        }
    }

    private void demarrerApercu() {
        if (apercu.isAvailable()) {
            ouvrirCameraArriere();
        } else {
            apercu.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override public void onSurfaceTextureAvailable(SurfaceTexture s, int w, int h) { ouvrirCameraArriere(); }
                @Override public void onSurfaceTextureSizeChanged(SurfaceTexture s, int w, int h) { }
                @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture s) { return true; }
                @Override public void onSurfaceTextureUpdated(SurfaceTexture s) { compterImage(); }
            });
        }
    }

    private void compterImage() {
        nbImages++;
        if (nbImages == 1 || nbImages % 30 == 0) images.setText("Images reçues : " + nbImages);
    }

    private void ouvrirCameraArriere() {
        apercu.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override public void onSurfaceTextureAvailable(SurfaceTexture s, int w, int h) { }
            @Override public void onSurfaceTextureSizeChanged(SurfaceTexture s, int w, int h) { }
            @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture s) { return true; }
            @Override public void onSurfaceTextureUpdated(SurfaceTexture s) { compterImage(); }
        });
        CameraManager cm = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String arriere = null;
            for (String id : cm.getCameraIdList()) {
                Integer face = cm.getCameraCharacteristics(id).get(CameraCharacteristics.LENS_FACING);
                if (face != null && face == CameraCharacteristics.LENS_FACING_BACK) arriere = id;
            }
            if (arriere == null) {
                etat.setText("Aucune caméra arrière");
                return;
            }
            cm.openCamera(arriere, new CameraDevice.StateCallback() {
                @Override public void onOpened(CameraDevice c) { camera = c; lancerSession(); }
                @Override public void onDisconnected(CameraDevice c) { c.close(); }
                @Override public void onError(CameraDevice c, int erreur) { etat.setText("Erreur caméra " + erreur); c.close(); }
            }, null);
        } catch (CameraAccessException | SecurityException e) {
            etat.setText("Erreur caméra : " + e.getMessage());
        }
    }

    private void lancerSession() {
        try {
            SurfaceTexture texture = apercu.getSurfaceTexture();
            texture.setDefaultBufferSize(1280, 720);
            Surface surface = new Surface(texture);
            CaptureRequest.Builder req = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            req.addTarget(surface);
            camera.createCaptureSession(Collections.singletonList(surface), new CameraCaptureSession.StateCallback() {
                @Override public void onConfigured(CameraCaptureSession s) {
                    try {
                        s.setRepeatingRequest(req.build(), null, null);
                        etat.setText("Aperçu caméra actif");
                    } catch (CameraAccessException e) {
                        etat.setText("Erreur caméra : " + e.getMessage());
                    }
                }
                @Override public void onConfigureFailed(CameraCaptureSession s) { etat.setText("Session caméra refusée"); }
            }, null);
        } catch (CameraAccessException e) {
            etat.setText("Erreur caméra : " + e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.close();
            camera = null;
        }
    }
}
