package com.example.simpleanimation1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

public class SpriteAnimationView extends View {
   private Bitmap bitmap;
   private Rect sourceRect;   // Prostokąt do wycinania klatki
   private int frameNr;       // Liczba klatek w animacji
   private int currentFrame;  // Bieżąca klatka
   private long frameTicker;  // Czas od ostatniej zmiany klatki
   private int framePeriod;   // Czas trwania jednej klatki w milisekundach

   private int spriteWidth;   // Szerokość pojedynczej klatki
   private int spriteHeight;  // Wysokość pojedynczej klatki

   public SpriteAnimationView(Context context, int resourceId, int frameCount, int fps) {
      super(context);
      frameNr = frameCount;
      framePeriod = 1000 / fps;
      frameTicker = 0l;
      currentFrame = 0;
      bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
      spriteWidth = bitmap.getWidth() / frameCount;
      spriteHeight = bitmap.getHeight();
      sourceRect = new Rect(0, 0, spriteWidth, spriteHeight);
   }

   @Override
   protected void onDraw(Canvas canvas) {
      // Aktualizuj bieżącą klatkę
      long gameTime = System.currentTimeMillis();
      if (gameTime > frameTicker + framePeriod) {
         frameTicker = gameTime;
         currentFrame = ++currentFrame % frameNr;
      }

      // Definiuj prostokąt wycinający klatkę
      this.sourceRect.left = currentFrame * spriteWidth;
      this.sourceRect.right = this.sourceRect.left + spriteWidth;

      // Rysuj bitmapę na canvasie
      Rect destRect = new Rect(getLeft(), getTop(), getLeft() + spriteWidth, getTop() + spriteHeight);
      canvas.drawBitmap(bitmap, sourceRect, destRect, null);
      invalidate(); // Wywołaj ponownie onDraw
   }
}