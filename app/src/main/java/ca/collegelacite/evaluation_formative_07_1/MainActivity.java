package ca.collegelacite.evaluation_formative_07_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // Définition des différents modes opératoires du toucher
    private enum ModeDeToucher { drag, drop, delete }

    // Mode opératoire actif du toucher
    private ModeDeToucher mode;

    // ImageView choisi pour dragging
    private ImageView droidChoisi = null;

    // Liste des ImageView instanciés durant l'exécution
    private List<ImageView> listeDroids = new ArrayList<ImageView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Installer un listener sur le layout de fond d'activité pour gérer
        // la manipulation des droids
        ConstraintLayout layout = findViewById(R.id.mainLayout);
        layout.setOnTouchListener(new ConstraintLayout.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent ev) {
                // Gérer la sélection et le déplacement des droids
                gérerOnTouchLayout(ev);
                return true;
            }
        });

        // Activer le mode opératoire par défaut: créer des droids
        setMode(ModeDeToucher.drop);
    }

    // create menu a start
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    //manage section in menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.drag:
                setMode(ModeDeToucher.drag);
                return true;
            case R.id.drop:
                setMode(ModeDeToucher.drop);
                return true;
            case R.id.delete:
                setMode(ModeDeToucher.delete);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Fonction gérant le toucher du layout de fond d'activité
    private void gérerOnTouchLayout(MotionEvent geste) {
        int x = (int) geste.getX();
        int y = (int) geste.getY();
        int action = geste.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mode == ModeDeToucher.drag) {
                    // Identifier le droid choisi par l'utilisateur
                    droidChoisi = droidSurPosition(x, y);
                }
                else if (mode == ModeDeToucher.drop){
                    // Créer un nouveau droid à la position touchée par l'utilisateur
                    droidChoisi = creerNouveauDroid(x, y);
                }
                else {
                    deleteDroid(droidSurPosition(x, y));
                }

                // Si un droid fut créé ou choisi, l'amener en avant-plan
                if (droidChoisi != null)
                    droidChoisi.bringToFront();

                break;

            case MotionEvent.ACTION_MOVE:
                // Si un droid est présentement choisi, le repositionner
                if (droidChoisi != null) {
                    droidChoisi.setX(x - droidChoisi.getWidth() / 2);
                    droidChoisi.setY(y - droidChoisi.getHeight() / 2);
                }
                break;

            case MotionEvent.ACTION_UP:
                // Arrêter de déplacer le droid choisi
                droidChoisi = null;
                break;

            default:
                break;
        }
    }

    // Fonction créant un nouveau droid (ImageView) de couleur aléatoire et positionné
    // aux coordonnées fournies
    private ImageView creerNouveauDroid(int x, int y) {
        // Taille d'un droid en pixels (selon l'image dans les ressources)
        final int droidSize = 144;

        // Obtenir le layout racine auquel on va ajouter un nouveau ImageView
        ConstraintLayout layout = findViewById(R.id.mainLayout);

        // Créer un ImageView et lui attribuer l'image du droid récupéré des ressources
        ImageView droid = new ImageView(this);
        droid.setImageResource(R.drawable.android);

        // Attribuer une couleur aléatoire au droid
        droid.setColorFilter(couleurAléatoire());

        // Ajouter le ImageView au layout racine de l'activité
        layout.addView(droid);

        // Ajuster la taille du droid
        droid.setLayoutParams(new ConstraintLayout.LayoutParams(droidSize, droidSize));

        // Centrer le droid à la position donnée en tenant compte du padding du layout
        droid.setX(x - droidSize / 2 - layout.getPaddingLeft());
        droid.setY(y - droidSize / 2 - layout.getPaddingTop());

        // Stocker le ImageView dans la liste afin de pouvoir le récupérer pour sa sélection
        listeDroids.add(droid);

        return droid;
    }

    // Retourne une couleur choisie au hasard parmi huit
    private int couleurAléatoire() {
        Random rand = new Random();
        int idx = rand.nextInt(8);
        switch (idx) {
            case 0: return Color.RED;
            case 1: return Color.GREEN;
            case 2: return Color.BLUE;
            case 3: return Color.YELLOW;
            case 4: return Color.BLACK;
            case 5: return Color.MAGENTA;
            case 6: return Color.GRAY;
            default: return Color.CYAN;
        }
    }
    // Parcours la liste des droids (ImageView) existants afin d'identifier le premier
    // positionné aux coordonnées fournies
    private ImageView droidSurPosition(int x, int y) {
        for (ImageView droid : listeDroids)
            if (pointSurView(x, y, droid))
                return droid;

        return null;  // aucun droid à la position donnée
    }

    //juste add delete to practice
    private void deleteDroid(ImageView droid){
        if (droid != null){
            droid.setImageResource(0);
        }
    }

    // Fonction indiquant si le View donné est visible aux coordonnées fournies
    private static boolean pointSurView(int x, int y, View droid) {
        float left = droid.getX();
        float right = droid.getX() + droid.getWidth();
        float top = droid.getY();
        float bottom = droid.getY() + droid.getHeight();

        if (x >= left && x <= right && y >= top && y <= bottom)
            return true;
        else
            return false;
    }

    // Change le mode opératoire du toucher
    private void setMode(ModeDeToucher nouveauMode) {
        mode = nouveauMode;

        // Indiquer le mode actif à l'aide du TextView
        TextView tv = findViewById(R.id.modeDisplayTextView);
        if (mode == ModeDeToucher.drag)
            tv.setText("DRAG");
        else
            tv.setText("DROP");
    }
}