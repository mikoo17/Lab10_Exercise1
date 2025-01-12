package com.example.simpleanimation1;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
   private enum RockfordDo {stand,idle,left,right,appear,die};

   private int frameCount = 7; // Ilość klatek w spritesheecie
   private int frameWidth; // szerokość pojedynczej klatki

   private GridLayout boardGridLayout; // GridLayout dla planszy
   private Character mapa[][]={
         {'b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b'},
         {'b','g','s','g','g','s','d','g','w','g','s','g','g','g','s','g','w','g','g','g','e','g','s','w','g','g','e','g','g','b'},
         {'b','g','g','g','g','g','g','g','w','g','g','g','g','g','g','g','w','g','g','g','e','g','g','w','g','g','g','g','g','b'},
         {'b','e','e','e','e','e','e','s','e','e','s','e','e','e','e','s','e','s','s','e','e','e','e','e','s','e','e','s','g','b'},
         {'b','d','g','g','g','g','g','g','w','g','s','g','g','g','g','s','w','g','s','g','e','g','g','w','g','g','g','s','d','b'},
         {'b','g','g','g','g','g','g','m','w','g','s','g','g','g','g','s','w','g','s','g','e','g','g','w','g','g','g','s','d','b'},
         {'b','w','w','w','w','w','w','w','w','w','w','w','w','w','w','w','w','g','w','w','e','w','w','w','w','w','w','w','w','b'},
         {'b','g','s','g','g','s','d','g','w','g','s','g','g','g','s','g','w','g','g','g','e','g','s','w','g','g','e','g','g','b'},
         {'b','g','g','g','g','g','g','g','w','g','g','g','g','g','g','g','w','g','g','g','e','g','g','w','g','g','g','g','g','b'},
         {'b','e','e','e','e','e','e','e','e','e','e','e','e','e','e','s','e','s','s','e','e','e','e','e','s','e','e','s','g','b'},
         {'b','m','g','g','g','g','g','s','w','g','s','g','g','g','g','s','w','g','s','g','e','g','g','w','g','g','g','s','d','b'},
         {'b','g','g','g','d','g','s','s','w','g','s','g','g','g','g','s','w','g','s','g','e','g','g','w','g','g','g','s','d','b'},
         {'b','w','w','w','w','w','w','w','w','w','w','w','w','w','w','w','w','g','w','w','e','w','w','w','w','w','w','w','w','b'},
         {'b','g','s','g','g','s','d','g','w','g','s','g','g','g','s','g','w','g','g','g','e','g','s','w','g','g','e','g','g','b'},
         {'b','g','g','g','g','g','g','g','w','g','g','g','g','g','g','g','w','g','g','g','e','g','g','w','g','g','g','g','g','b'},
         {'b','e','e','e','e','e','e','e','e','e','e','e','e','e','e','s','e','s','s','e','e','e','e','e','s','e','e','s','g','b'},
         {'b','g','g','g','g','g','g','g','w','g','s','g','g','g','g','s','w','g','s','g','e','g','g','w','g','g','g','s','d','b'},
         {'b','g','g','g','g','g','g','m','w','g','s','g','g','g','g','s','w','g','s','g','e','g','g','w','g','g','g','s','d','b'},
         {'b','w','w','w','w','w','w','w','w','w','w','w','w','w','w','w','w','g','w','w','e','w','w','w','w','w','w','w','w','b'},


         {'b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b'},
      }; //mapa

   private ImageView imageView;
   private int frameIndex = 0;
   private Handler handler;
   private Bitmap spritesheet_rockford_stand;
   private Bitmap spritesheet_rockford_idle;
   private Bitmap spritesheet_rockford_left;
   private Bitmap spritesheet_rockford_right;
   private TextView scoreTextView;
   private TextView livesTextView;
   private RockfordDo rockfordDo = RockfordDo.stand;
   private int idleTimer=0;
   private int idletime=40;
   private int scale = 1; // Współczynnik powiększenia
   private float imageX=88 , imageY =88;  // współrzędne Rockforda
   private boolean movingUp, movingDown, movingLeft, movingRight;
   private float deltaX = 82; // przesunięcie Rockforda prawo lewo
   private float deltaY = 82; // przesunięcie Rockforda góra dół
   private int score = 0;  // Liczba punktów
   private int lives = 3;  // Liczba żyć
   private float boardOffsetX = 0f; // Przesunięcie planszy w poziomie
   private float boardOffsetY = 0f; // Przesunięcie planszy w pionie
   private FrameLayout mainLayout;

   // Definicja obszarów sterowania
   private View topControl, bottomControl, leftControl, rightControl;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      mainLayout = findViewById(R.id.mainLayout); // Pobieramy RelativeLayout z layoutu
      mainLayout.setBackgroundColor(getResources().getColor(android.R.color.black)); // Ustawiamy czarne tło
      // Inicjalizacja planszy w GridLayout
      initBoardGridLayout();

      scoreTextView = findViewById(R.id.scoreTextView);
      livesTextView = findViewById(R.id.livesTextView);

      imageView = findViewById(R.id.sprite_view);
      handler = new Handler();
      spritesheet_rockford_stand = BitmapFactory.decodeResource(getResources(),R.drawable.rockford_stand_32x32);
      spritesheet_rockford_idle = BitmapFactory.decodeResource(getResources(),R.drawable.rockford_idle_32x32);
      spritesheet_rockford_left = BitmapFactory.decodeResource(getResources(),R.drawable.rockford_left_32x32);
      spritesheet_rockford_right = BitmapFactory.decodeResource(getResources(),R.drawable.rockford_right_32x32);
      // Obliczamy szerokość pojedynczej klatki
      frameWidth = spritesheet_rockford_stand.getWidth() / frameCount;
      // Ustawiamy szerokość ImageView na szerokość pojedynczej klatki, pomnożoną przez współczynnik powiększenia
      imageView.getLayoutParams().width = frameWidth * scale;
      imageView.getLayoutParams().height = spritesheet_rockford_stand.getHeight() * scale; // Ustawiamy wysokość proporcjonalnie do powiększenia
      // imageView.requestLayout();
      // Inicjalizacja obszarów sterowania
      topControl = findViewById(R.id.controlTop);
      bottomControl = findViewById(R.id.controlDown);
      leftControl = findViewById(R.id.controlLeft);
      rightControl = findViewById(R.id.controlRight);
      // Ustawiamy obszary sterowania jako dotykowe
      setTouchListeners();
      // Uruchamiamy animację
      startAnimation();
   }

   private void initBoardGridLayout() {
      // Tworzymy planszę w GridLayout
      boardGridLayout = new GridLayout(this);
      boardGridLayout.setColumnCount(mapa[0].length); // liczba kolumn
      boardGridLayout.setRowCount(mapa.length);    // liczba wierszy
      // Tworzymy obiekty ImageView i dodajemy je do planszy
      for (int i = 0; i < mapa.length; i++) {
         for (int j = 0; j < mapa[0].length; j++) {
            ImageView imageView = new ImageView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            //params.height = 64;
            //params.width = 64;
            //imageView.setLayoutParams(params);
            switch (mapa[i][j]){
               case 'b':
                  imageView.setImageResource(R.drawable.border_32x32);                  break;
               case 'w':
                  imageView.setImageResource(R.drawable.wall_32x32);                  break;
               case 's':
                  imageView.setImageResource(R.drawable.stone_32x32);                  break;
               case 'g':
                  imageView.setImageResource(R.drawable.ground_32x32);                  break;
               case 'e':
                  imageView.setImageResource(R.drawable.empty_32x32);                  break;
               case 'm':
                  imageView.setImageResource(R.drawable.mob_32x32);                  break;
               case 'd':
                  imageView.setImageResource(R.drawable.diamond_32x32);                  break;
               case 'x':
                  imageView.setImageResource(R.drawable.butterfly_32x32);                  break;
            }
            boardGridLayout.addView(imageView);
         }
      }
      // Pobieramy FrameeLayout z layoutu
      FrameLayout mainLayout = findViewById(R.id.mainLayout);
      // Dodajemy planszę do RelativeLayout
      mainLayout.addView(boardGridLayout,0);
   }

   private void setTouchListeners() {
      topControl.setOnTouchListener(new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
               case MotionEvent.ACTION_DOWN:
                  movingUp = true;
                  idleTimer=0;
                  break;
               case MotionEvent.ACTION_UP:
                  movingUp = false;
                  rockfordDo=RockfordDo.stand;
                  break;
            }
            return true;
         }
      });

      bottomControl.setOnTouchListener(new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
               case MotionEvent.ACTION_DOWN:
                  movingDown = true;
                  idleTimer=0;
                  break;
               case MotionEvent.ACTION_UP:
                  movingDown = false;
                  rockfordDo=RockfordDo.stand;
                  break;
            }
            return true;
         }
      });

      leftControl.setOnTouchListener(new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
               case MotionEvent.ACTION_DOWN:
                  movingLeft = true;
                  idleTimer=0;
                  break;
               case MotionEvent.ACTION_UP:
                  movingLeft = false;
                  rockfordDo=RockfordDo.stand;
                  break;
            }
            return true;
         }
      });

      rightControl.setOnTouchListener(new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
               case MotionEvent.ACTION_DOWN:
                  movingRight = true;
                  idleTimer=0;
                  break;
               case MotionEvent.ACTION_UP:
                  movingRight = false;
                  rockfordDo=RockfordDo.stand;
                  break;
            }
            return true;
         }
      });
   }

   private void moveRockford() {

      int[] position = getRockfordPosition();
      int row = position[0];
      int col = position[1];

       // Pobierz ImageView dla pola

      if (movingUp && row > 0 && isPassable(mapa[row - 1][col])) {
         imageY -= deltaY;
         row--;
      } else if (movingDown && row < mapa.length - 1 && isPassable(mapa[row + 1][col])) {
         imageY += deltaY;
         row++;
      } else if (movingLeft && col > 0 && isPassableHorizontal(mapa[row][col - 1])) {
         if(mapa[row][col - 1] == 's'){
            if (mapa[row][col - 2] =='e')
            {
               moveStone(true);

               imageX -= deltaX;
               col--;
            }

         }
         else {
            imageX -= deltaX;
            col--;
         }
      } else if (movingRight && col < mapa[0].length - 1 && isPassableHorizontal(mapa[row][col + 1])) {

         if(mapa[row][col + 1] == 's'){
            if (mapa[row][col + 2] =='e')
            {
               moveStone(false);

               imageX += deltaX;
               col++;
            }

         }
         else {
            imageX += deltaX;
            col++;
         }

      }

      char currentCell = mapa[row][col];
      switch (currentCell) {
         case 'g': // Zjada ziemię
            mapa[row][col] = 'e';
            updateBoardCell(row, col);
            break;
         case 'm': // Kolizja z mobem
            mapa[row][col] = 'e';
            updateBoardCell(row, col);
            loseLife();
         case 'x': // Kolizja z motylem
            mapa[row][col] = 'e';
            updateBoardCell(row, col);
            loseLife();
            break;
         case 'd': // Kolizja z diamentem
            mapa[row][col] = 'e';
            updateBoardCell(row, col);
            increaseScore();
            break;
      }

      // Przesuwanie kamery
      adjustCamera();

      imageView.setX(imageX);
      imageView.setY(imageY);
   }
   private void moveStone(boolean left){
      int[] position = getRockfordPosition();
      int row = position[0];
      int col = position[1];
      int first = left ? -1 : 1;
      int second = left ? -2 : 2;

      mapa[row][col+first] = 'e';
      mapa[row][col+second] = 's';


      ImageView empty_cell = (ImageView) boardGridLayout.getChildAt( row * mapa[0].length + col + first);
      empty_cell.setImageResource(R.drawable.empty_32x32);

      ImageView stone_cell = (ImageView) boardGridLayout.getChildAt( row * mapa[0].length + col + second);
      stone_cell.setImageResource(R.drawable.stone_32x32);
   }


   private void startAnimation() {
      Runnable animationRunnable = new Runnable() {
         @Override
         public void run() {
            // Ustawiamy kolejną klatkę animacji
            // Wycinamy fragment z spritesheeta odpowiadający danej klatce i ustawiamy w ImageView
            Bitmap frame;
            switch (rockfordDo){
            case stand:
               frame = Bitmap.createBitmap(spritesheet_rockford_stand, frameIndex * frameWidth, 0, frameWidth, spritesheet_rockford_stand.getHeight());
               frame = Bitmap.createScaledBitmap(frame, frameWidth * scale, spritesheet_rockford_stand.getHeight() * scale, true);
               moveRockford();
               idleTimer++;
               imageView.setImageBitmap(frame);
               if (idleTimer>20) {rockfordDo=RockfordDo.idle;}
               break;
            case idle:
               frame = Bitmap.createBitmap(spritesheet_rockford_idle, frameIndex * frameWidth, 0, frameWidth, spritesheet_rockford_idle.getHeight());
               frame = Bitmap.createScaledBitmap(frame, frameWidth * scale, spritesheet_rockford_idle.getHeight() * scale, true);
               moveRockford();
               imageView.setImageBitmap(frame);
               break;
            case right:
               frame = Bitmap.createBitmap(spritesheet_rockford_right, frameIndex * frameWidth, 0, frameWidth, spritesheet_rockford_right.getHeight());
               frame = Bitmap.createScaledBitmap(frame, frameWidth * scale, spritesheet_rockford_right.getHeight() * scale, true);
               moveRockford();
               imageView.setImageBitmap(frame);
               idleTimer=0;
               break;
            case left:
               frame = Bitmap.createBitmap(spritesheet_rockford_left, frameIndex * frameWidth, 0, frameWidth, spritesheet_rockford_left.getHeight());
               frame = Bitmap.createScaledBitmap(frame, frameWidth * scale, spritesheet_rockford_left.getHeight() * scale, true);
               moveRockford();
               imageView.setImageBitmap(frame);
               idleTimer=0;
               break;
         }
            // Inkrementujemy indeks klatki
            frameIndex = (frameIndex + 1) % frameCount;
            // Wywołujemy ponownie runnable po pewnym czasie dla następnej klatki
            handler.postDelayed(this, 100); // Ustaw czas opóźnienia między klatkami (tutaj 100ms)
         }
      };
      handler.post(animationRunnable);
      applyGravity();
   }

   private void updateBoardCell(int row, int col) {
      int index = row * mapa[0].length + col; // Oblicz indeks w GridLayout
      ImageView cell = (ImageView) boardGridLayout.getChildAt(index); // Pobierz ImageView dla pola
      switch (mapa[row][col]) {
         case 'e':
            cell.setImageResource(R.drawable.empty_32x32);
            break;
         case 'g':
            cell.setImageResource(R.drawable.ground_32x32);
            break;
         case 's':
            cell.setImageResource(R.drawable.stone_32x32);
            break;
         // Możesz dodać inne typy, jeśli to konieczne
      }
   }

   private int[] getRockfordPosition() {
      int col = (int) (imageX / deltaX); // Oblicz indeks kolumny
      int row = (int) (imageY / deltaY); // Oblicz indeks wiersza
      return new int[]{row, col};
   }

   private boolean isPassable(char cell) {
      return cell == 'e' || cell == 'g' || cell == 'd' || cell == 'm'; // Rockford może przejść tylko przez te typy

   }

   private boolean isPassableHorizontal(char cell) {
      return cell == 'e' || cell == 'g' || cell == 'd' || cell == 'm' || cell == 's'; // Rockford może przejść tylko przez te typy

   }

   private void loseLife() {
      // Zmniejsz życie gracza
      lives--;
      if (lives <= 0) {
         endGame();
      }
      livesTextView.setText("Życia: " + lives);

   }

   private void increaseScore() {
      score += 10; // Zwiększ liczbę punktów
      scoreTextView.setText("Punkty: " + score);
   }

   private void endGame() {
      // Wyświetl komunikat o przegranej i zakończ grę
      handler.removeCallbacksAndMessages(null); // Zatrzymaj animację
      finish();
   }

   private void applyGravity() {
      Runnable gravityRunnable = new Runnable() {
         @Override
         public void run() {
            // Przesuwamy kamienie w dół, jeden wiersz na raz
            for (int row = mapa.length - 2; row >= 0; row--) {
               for (int col = 0; col < mapa[0].length; col++) {
                  // Sprawdzamy, czy komórka zawiera kamień
                  if (mapa[row][col] == 's' && mapa[row + 1][col] == 'e') {
                     // Zamieniamy kamień i puste pole
                     mapa[row + 1][col] = 's';
                     mapa[row][col] = 'e';
                     updateBoardCell(row, col); // Uaktualniamy planszę
                     updateBoardCell(row + 1, col); // Uaktualniamy planszę

                     // Jeśli kamień spada na Rockforda
                     if (imageY == (row + 1) * deltaY && imageX == col * deltaX) {
                        loseLife();
                     }
                  }
               }
            }

            // Ponownie wywołujemy grawitację po pewnym czasie (co 300ms)
            handler.postDelayed(this, 300);
         }
      };

      // Uruchamiamy grawitację
      handler.post(gravityRunnable);
   }

   private void adjustCamera() {
      int screenCenterX = mainLayout.getWidth() / 2;
      int screenCenterY = mainLayout.getHeight() / 2;

      float rockfordScreenX = imageX - boardOffsetX;
      float rockfordScreenY = imageY - boardOffsetY;

      // Przesuwanie planszy w lewo
      if (imageX < 100) {
         boardOffsetX -= deltaX;
         imageX += deltaX; // Aktualizacja pozycji postaci
      }

      // Przesuwanie planszy w prawo
      if (imageX > mainLayout.getWidth() - 100) {
         boardOffsetX += deltaX;
         imageX -= deltaX; // Aktualizacja pozycji postaci
      }

      // Przesuwanie planszy w górę
      if (imageY < 100) {
         boardOffsetY -= deltaY;
         imageY += deltaY; // Aktualizacja pozycji postaci
      }

      // Przesuwanie planszy w dół
      if (imageY > mainLayout.getHeight() - 100) {
         boardOffsetY += deltaY;
         imageY -= deltaY; // Aktualizacja pozycji postaci
      }

      // Debug - Wyświetlanie pozycji na ekranie
      System.out.println("rockfordScreenX: " + rockfordScreenX + ", rockfordScreenY: " + rockfordScreenY);
      System.out.println("screenCenterX: " + screenCenterX + ", screenCenterY: " + screenCenterY);

      // Zastosuj przesunięcie do planszy
      boardGridLayout.setTranslationX(-boardOffsetX);
      boardGridLayout.setTranslationY(boardOffsetY);
   }







   @Override
   protected void onDestroy() {
      super.onDestroy();
      handler.removeCallbacksAndMessages(null);
      spritesheet_rockford_stand.recycle(); // Zwolnij zasoby bitmapy spritesheeta
      spritesheet_rockford_left.recycle(); // Zwolnij zasoby bitmapy spritesheeta
      spritesheet_rockford_right.recycle(); // Zwolnij zasoby bitmapy spritesheeta
   }
}