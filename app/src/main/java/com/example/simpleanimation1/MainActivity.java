package com.example.simpleanimation1;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.media.SoundPool;
import android.media.AudioAttributes;

public class MainActivity extends AppCompatActivity {
   private enum RockfordDo {stand,idle,left,right,appear,die};

   private int frameCount = 7; // Ilość klatek w spritesheecie
   private int frameWidth; // szerokość pojedynczej klatki

   private SoundPool soundPool;
   private int soundStep1, soundStep2, soundMainTheme, soundDiamondCollect, soundDiamondFall, soundExitOpen, soundRockfordAppear, soundStartGame, soundStoneFall;
   private boolean isExitOpen = false;
   int VISIBLE_AREA_WIDTH;
   int VISIBLE_AREA_HEIGHT;
   private int GRID_CELL_SIZE = 64;

   private GridLayout boardGridLayout; // GridLayout dla planszy
   private Character mapa[][]={
         {'b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b'},
         {'b','g','s','g','g','s','d','g','w','g','s','g','g','g','s','g','w','g','g','g','e','g','s','w','g','g','e','g','g','b'},
         {'b','g','g','g','g','g','g','g','w','g','g','g','g','g','g','g','w','g','g','g','e','g','g','w','g','g','g','g','g','b'},
         {'b','e','e','e','e','e','e','s','e','e','s','e','e','e','e','s','e','g','e','e','e','e','e','e','g','e','e','s','g','b'},
         {'b','d','g','g','g','g','g','g','w','g','s','g','g','g','g','s','w','g','s','g','e','g','g','w','g','g','g','s','d','b'},
         {'b','g','g','g','g','g','g','m','w','g','s','g','g','g','g','s','w','g','s','g','e','g','g','w','g','g','g','s','d','b'},
         {'b','w','w','g','w','w','w','w','w','w','w','w','w','w','w','w','w','g','w','w','e','w','w','w','w','w','w','w','w','b'},
         {'b','g','s','g','g','s','d','g','w','g','s','g','g','g','s','g','w','g','g','g','e','g','s','w','g','g','e','g','g','b'},
         {'b','g','g','g','g','g','g','g','w','g','g','g','g','g','g','g','w','g','g','g','e','g','g','w','g','g','g','g','g','b'},
         {'b','e','e','e','e','e','e','e','e','e','e','e','e','e','e','s','e','e','e','e','e','e','e','e','e','e','e','s','g','b'},
         {'b','m','g','g','g','g','g','s','w','g','s','g','g','g','g','s','w','g','s','g','e','g','g','w','g','g','g','s','d','b'},
         {'b','g','g','g','d','g','s','s','w','g','s','g','g','g','g','s','w','g','s','g','e','g','g','w','g','g','g','s','d','b'},
         {'b','w','w','g','w','w','w','w','w','w','w','w','w','w','w','w','w','g','w','w','e','w','w','w','w','w','w','w','w','b'},
         {'b','g','s','g','g','s','d','g','w','g','s','g','g','g','s','g','w','g','g','g','e','g','s','w','g','g','e','g','g','b'},
         {'b','g','g','g','g','g','g','g','w','g','g','g','g','g','g','g','w','g','g','g','e','g','g','w','g','g','g','g','g','b'},
         {'b','e','e','e','e','e','e','e','e','e','e','e','e','e','e','s','e','s','s','e','e','e','e','e','s','e','e','s','g','b'},
         {'b','g','g','g','g','g','g','g','w','g','s','g','g','g','g','s','w','g','s','g','e','g','g','w','g','g','g','s','d','b'},
         {'b','g','g','g','g','g','g','m','w','o','s','g','g','g','g','s','w','g','s','g','e','g','g','w','g','g','g','s','d','b'},
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
   private float imageX=64 , imageY =64;  // współrzędne Rockforda
   private boolean movingUp, movingDown, movingLeft, movingRight;
   private float deltaX = 64; // przesunięcie Rockforda prawo lewo
   private float deltaY = 64; // przesunięcie Rockforda góra dół
   private int score = 0;  // Liczba punktów
   private int lives = 3;  // Liczba żyć
   private float boardOffsetX = 0f; // Przesunięcie planszy w poziomie
   private float boardOffsetY = 0f; // Przesunięcie planszy w pionie
   private FrameLayout mainLayout;


   private View topControl, bottomControl, leftControl, rightControl;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      mainLayout = findViewById(R.id.mainLayout); // Pobieramy RelativeLayout z layoutu
      mainLayout.setBackgroundColor(getResources().getColor(android.R.color.black)); // Ustawiamy czarne tło
      initBoardGridLayout();

      scoreTextView = findViewById(R.id.scoreTextView);
      livesTextView = findViewById(R.id.livesTextView);

      imageView = findViewById(R.id.sprite_view);
      handler = new Handler();
      spritesheet_rockford_stand = BitmapFactory.decodeResource(getResources(),R.drawable.rockford_stand_32x32);
      spritesheet_rockford_idle = BitmapFactory.decodeResource(getResources(),R.drawable.rockford_idle_32x32);
      spritesheet_rockford_left = BitmapFactory.decodeResource(getResources(),R.drawable.rockford_left_32x32);
      spritesheet_rockford_right = BitmapFactory.decodeResource(getResources(),R.drawable.rockford_right_32x32);

      frameWidth = spritesheet_rockford_stand.getWidth() / frameCount;

      imageView.getLayoutParams().width = frameWidth * scale;
      imageView.getLayoutParams().height = spritesheet_rockford_stand.getHeight() * scale; // Ustawiamy wysokość proporcjonalnie do powiększenia
      imageView.requestLayout();
      // Inicjalizacja obszarów sterowania
      topControl = findViewById(R.id.controlTop);
      bottomControl = findViewById(R.id.controlDown);
      leftControl = findViewById(R.id.controlLeft);
      rightControl = findViewById(R.id.controlRight);

      VISIBLE_AREA_WIDTH = mainLayout.getWidth();
      VISIBLE_AREA_HEIGHT = mainLayout.getHeight();

      AudioAttributes audioAttributes = new AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_GAME)
              .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
              .build();

      soundPool = new SoundPool.Builder()
              .setMaxStreams(10)
              .setAudioAttributes(audioAttributes)
              .build();

      soundStep1 = soundPool.load(this, R.raw.krok, 1);
      soundStep2 = soundPool.load(this, R.raw.krok2, 1);
      soundMainTheme = soundPool.load(this, R.raw.main_theme, 1);
      soundDiamondCollect = soundPool.load(this, R.raw.diamond_collect, 1);
      soundDiamondFall = soundPool.load(this, R.raw.diamond_fall, 1);
      soundExitOpen = soundPool.load(this, R.raw.exit_open, 1);
      soundRockfordAppear = soundPool.load(this, R.raw.rockford_appear, 1);
      soundStartGame = soundPool.load(this, R.raw.start_game, 1);
      soundStoneFall = soundPool.load(this, R.raw.stone_fall, 1);

      playSound(soundMainTheme);

      setTouchListeners();
      startAnimation();
   }

   private void initBoardGridLayout() {
      boardGridLayout = new GridLayout(this);
      boardGridLayout.setColumnCount(mapa[0].length); // liczba kolumn
      boardGridLayout.setRowCount(mapa.length);       // liczba wierszy
      int height = Math.max(mapa.length * GRID_CELL_SIZE, 1920);

      // Ustawiamy LayoutParams dla boardGridLayout
      FrameLayout.LayoutParams gridLayoutParams = new FrameLayout.LayoutParams(
              mapa[0].length * GRID_CELL_SIZE,
              height
      );
      boardGridLayout.setLayoutParams(gridLayoutParams);

      // Tworzymy obiekty ImageView i dodajemy je do planszy
      for (int i = 0; i < mapa.length; i++) {
         for (int j = 0; j < mapa[0].length; j++) {
            ImageView imageView = new ImageView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = GRID_CELL_SIZE;
            params.height = GRID_CELL_SIZE;
            imageView.setLayoutParams(params);
            imageView.setVisibility(View.VISIBLE);

            switch (mapa[i][j]) {
               case 'b':
                  imageView.setImageResource(R.drawable.border_32x32);
                  break;
               case 'w':
                  imageView.setImageResource(R.drawable.wall_32x32);
                  break;
               case 's':
                  imageView.setImageResource(R.drawable.stone_32x32);
                  break;
               case 'g':
                  imageView.setImageResource(R.drawable.ground_32x32);
                  break;
               case 'e':
                  imageView.setImageResource(R.drawable.empty_32x32);
                  break;
               case 'm':
                  imageView.setImageResource(R.drawable.mob_32x32);
                  break;
               case 'd':
                  imageView.setImageResource(R.drawable.diamond_32x32);
                  break;
               case 'x':
                  imageView.setImageResource(R.drawable.butterfly_32x32);
                  break;

               case 'o':
                  imageView.setImageResource(R.drawable.exit_closed);
                  break;
            }

                 boardGridLayout.addView(imageView);
         }
      }

      // Znajdujemy główny layout
      FrameLayout mainLayout = findViewById(R.id.mainLayout);

      // Sprawdzamy LayoutParams dla mainLayout i ustawiamy jego rozmiar
      if (mainLayout.getLayoutParams() == null) {
         mainLayout.setLayoutParams(new FrameLayout.LayoutParams(
                 FrameLayout.LayoutParams.WRAP_CONTENT,
                 FrameLayout.LayoutParams.WRAP_CONTENT
         ));
      }
      ViewGroup.LayoutParams mainLayoutParams = mainLayout.getLayoutParams();
      mainLayoutParams.width = mapa[0].length * GRID_CELL_SIZE;
      mainLayoutParams.height = mapa.length * GRID_CELL_SIZE;
      mainLayout.setLayoutParams(mainLayoutParams);

      mainLayout.addView(boardGridLayout, 0);
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
      boolean ismoving = false;
       int GRID_CELL_SIZE = 64; // Rozmiar jednej komórki planszy (w pikselach)

      if (movingUp && row > 0 && isPassable(mapa[row - 1][col])) {
         imageY -= deltaY;
         row--;
         ismoving = true;
         playSound(soundStep1);
      } else if (movingDown && row < mapa.length - 1 && isPassable(mapa[row + 1][col])) {
         imageY += deltaY;
         row++;
         ismoving = true;
         playSound(soundStep2);
      } else if (movingLeft && col > 0 && isPassableHorizontal(mapa[row][col - 1])) {
         if(mapa[row][col - 1] == 's'){
            if (mapa[row][col - 2] =='e')
            {
               moveStone(true);

               imageX -= deltaX;
               col--;
               ismoving = true;
               playSound(soundStep1);
            }
         }
         else {
            imageX -= deltaX;
            col--;
            ismoving = true;
            playSound(soundStep1);
         }
      } else if (movingRight && col < mapa[0].length - 1 && isPassableHorizontal(mapa[row][col + 1])) {

         if(mapa[row][col + 1] == 's'){
            if (mapa[row][col + 2] =='e')
            {
               moveStone(false);

               imageX += deltaX;
               col++;
               ismoving = true;
               playSound(soundStep2);
            }
         }
         else {
            imageX += deltaX;
            col++;
            ismoving = true;
            playSound(soundStep2);
         }
      }
      if (ismoving) moveCamera();


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
            playSound(soundDiamondCollect);
            break;
         case 'o':
            if(isExitOpen){
               winGame();
            }
      }
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

   private void moveCamera(){
      int OFFSET_THRESHOLD = GRID_CELL_SIZE * 2; // Próg przesunięcia mapy
      if (imageX < 64*4){
         boardOffsetX += 64*6;
         boardGridLayout.setTranslationX(boardOffsetX);
         imageX += 64*6;
      }
      else if(imageX > 1080 -64*4){
         boardOffsetX -= 64*6;
         boardGridLayout.setTranslationX(boardOffsetX);
         imageX -= 64*6;
      }

      if (imageY <64){
         boardOffsetY += 64*6;
         boardGridLayout.setTranslationY(boardOffsetY);
         imageY +=64*6;
      }
      else if(imageY > 1920 -64*3){
         boardOffsetY -= 64*6;
         boardGridLayout.setTranslationY(boardOffsetY);
         imageY -= 64*6;
      }

   }

   private void startAnimation() {
      Runnable animationRunnable = new Runnable() {
         @Override
         public void run() {
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
            frameIndex = (frameIndex + 1) % frameCount;
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

      }
   }

   private int[] getRockfordPosition() {
      int row = (int) ((imageY - boardOffsetY) / 64);
      int col = (int) ((imageX - boardOffsetX) / 64);
      return new int[]{row, col};
   }

   private boolean isPassable(char cell) {
      return cell == 'e' || cell == 'g' || cell == 'd' || cell == 'm' || cell =='o'; // Rockford może przejść tylko przez te typy

   }

   private boolean isPassableHorizontal(char cell) {
      return cell == 'e' || cell == 'g' || cell == 'd' || cell == 'm' || cell == 's'|| cell =='o'; // Rockford może przejść tylko przez te typy

   }

   private void loseLife() {
      lives--;
      if (lives <= 0) {
         endGame();
      }
      livesTextView.setText("Życia: " + lives);

   }

   private void increaseScore() {
      score += 10; // Zwiększ liczbę punktów
      scoreTextView.setText("Punkty: " + score);

      if(score == 110) openGate();
   }

   private void endGame() {
      handler.removeCallbacksAndMessages(null); // Zatrzymaj animację

      // Pobierz odniesienie do TextView
      TextView gameOverText = findViewById(R.id.gameOverText);

      // Ustaw widoczność napisu "Game Over"
      gameOverText.setVisibility(View.VISIBLE);

      closeApp();
   }
   private void openGate(){
      int index = 519;
      ImageView cell = (ImageView) boardGridLayout.getChildAt(index); // Pobierz ImageView dla pola
      cell.setImageResource(R.drawable.exit_open);
      playSound(soundExitOpen);
      isExitOpen = true;
   }
   private void winGame() {
      handler.removeCallbacksAndMessages(null); // Zatrzymaj animację

      // Pobierz odniesienie do TextView
      TextView winText = findViewById(R.id.winText);

      // Ustaw widoczność napisu "You Win!"
      winText.setVisibility(View.VISIBLE);

      closeApp();
   }

   private void closeApp(){
      // Dodatkowo możesz dodać opóźnienie, aby dać czas na zobaczenie napisu przed zamknięciem gry
      new Handler().postDelayed(new Runnable() {
         @Override
         public void run() {
            // Zamknij grę po 2 sekundach
            finish();
         }
      }, 2000); // 2000 ms = 2 sekundy
   }


   private void applyGravity() {
      Runnable gravityRunnable = new Runnable() {
         @Override
         public void run() {
            // Get Rockford's position (row and col)
            int[] position = getRockfordPosition();
            int rockfordRow = position[0];
            int rockfordCol = position[1];

            // Przesuwamy kamienie w dół, jeden wiersz na raz
            for (int row = mapa.length - 2; row >= 0; row--) {
               for (int col = 0; col < mapa[0].length; col++) {
                  // Sprawdzamy, czy komórka zawiera kamień
                  if (mapa[row][col] == 's' && mapa[row + 1][col] == 'e') {
                     playSound(soundStoneFall);
                     // Zamieniamy kamień i puste pole
                     mapa[row + 1][col] = 's';
                     mapa[row][col] = 'e';
                     updateBoardCell(row, col); // Uaktualniamy planszę
                     updateBoardCell(row + 1, col); // Uaktualniamy planszę

                     // Jeśli kamień spada na Rockforda
                     if (rockfordRow == row + 1 && rockfordCol == col) {
                        loseLife();
                        playSound(soundStoneFall);
                     }
                  }
               }
            }

            // Ponownie wywołujemy grawitację po pewnym czasie (co 500ms)
            handler.postDelayed(this, 500);
         }
      };

      // Uruchamiamy grawitację
      handler.post(gravityRunnable);
   }


   // Funkcja odtwarzająca dźwięki
   private void playSound(int soundId) {
      soundPool.play(soundId, 1, 1, 1, 0, 1f);
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