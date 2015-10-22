package bokeh.live.wallpaper;

import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import bokeh.live.wallpaper.SettableMappedIndexPreference.Mapper;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class BokehWallpaper extends WallpaperService {
	public static final String SHARED_PREFS_NAME = "bokeh_settings";

//	public static final int DEFAULT_BOKEH_COUNT = 0;
//	public static final float DEFAULT_MIN_RADIUS = 20.0f;
//	public static final float DEFAULT_MAX_RADIUS = 40.0f;
//	public static final float DEFAULT_MIN_OUTLINE_WIDTH = 5.0f;
//	public static final float DEFAULT_MAX_OUTLINE_WIDTH = 10.0f;
//	public static final int DEFAULT_MIN_TRANSPARENCY = 96;
//	public static final int DEFAULT_MAX_TRANSPARENCY = 212;
//	public static final float DEFAULT_MIN_SPEED = 0.0f;
//	public static final float DEFAULT_MAX_SPEED = 0.7f;
//	public static final float DEFAULT_BRIGHTNESS = 0.4f;
//	public static final int DEFAULT_FRAME_RATE = 30;
	public static final int DEFAULT_BOKEH_COUNT = 30;
	public static final int DEFAULT_EFFECT_TYPE = 0;
	public static final int DEFAULT_MIN_RADIUS = 9;
	public static final int DEFAULT_MAX_RADIUS = 49;
	public static final int DEFAULT_MIN_OUTLINE_WIDTH = 3;
	public static final int DEFAULT_MAX_OUTLINE_WIDTH = 15;
	public static final int DEFAULT_MIN_TRANSPARENCY = 64;
	public static final int DEFAULT_MAX_TRANSPARENCY = 212;
	public static final int DEFAULT_MIN_SPEED = 0;
	public static final int DEFAULT_MAX_SPEED = 7;
	public static final int DEFAULT_BRIGHTNESS = 50;
	public static final int DEFAULT_FRAME_RATE = 29;
	public static final boolean DEFAULT_SLIGHT_BLUR = false;
	public static final boolean DEFAULT_RADIATING_WAVES = false;

	public static final int BOKEH_COUNT_MAX_INDEX = 990;
	public static final int EFFECT_TYPE_MAX_INDEX = 2;
	public static final int MIN_RADIUS_MAX_INDEX = 59;
	public static final int MAX_RADIUS_MAX_INDEX = 99;
	public static final int MIN_OUTLINE_WIDTH_MAX_INDEX = 60;
	public static final int MAX_OUTLINE_WIDTH_MAX_INDEX = 60;
	public static final int MIN_TRANSPARENCY_MAX_INDEX = 255;
	public static final int MAX_TRANSPARENCY_MAX_INDEX = 255;
	public static final int MIN_SPEED_MAX_INDEX = 40;
	public static final int MAX_SPEED_MAX_INDEX = 40;
	public static final int BRIGHTNESS_MAX_INDEX = 100;
	public static final int FRAME_RATE_MAX_INDEX = 99;
	
	private ArrayList<Bokeh> bokehs = new ArrayList<Bokeh>();
    private final Handler mHandler = new Handler();
    private Random random = new Random(System.currentTimeMillis());

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Engine onCreateEngine() {
        return new BokehEngine();
    }
    
    public static Mapper BOKEH_COUNT_MAPPER = new Mapper() {
    	@Override
    	public int calculateToInt(int index) {
        	return index + 10;
        }

    	@Override
    	public float calculateToFloat(int index) {
        	throw new UnsupportedOperationException("There's a mismatch between the various data types in the properties mapper.");
        }

    	@Override
		public String calculateToDisplayableString(int index) {
			return Integer.toString(calculateToInt(index));
		}
    };
    
    public static Mapper EFFECT_TYPE_MAPPER = new Mapper() {
    	@Override
    	public int calculateToInt(int index) {
        	return index;
        }

    	@Override
    	public float calculateToFloat(int index) {
        	throw new UnsupportedOperationException("There's a mismatch between the various data types in the properties mapper.");
        }

    	@Override
		public String calculateToDisplayableString(int index) {
    		int val = calculateToInt(index);
    		if (val == 0) {
    			return "Mellow bokeh";
    		}
    		else if (val == 1) {
    			return "Hyper bokeh";
    		}
    		else if (val == 2) {
    			return "Interference";
    		}
    		else {
    			return "";
    		}
//			return Integer.toString(calculateToInt(index));
		}
    };
    
    public static Mapper MIN_RADIUS_MAPPER = new Mapper() {
    	@Override
    	public int calculateToInt(int index) {
    		throw new UnsupportedOperationException("There's a mismatch between the various data types in the properties mapper.");
        }

    	@Override
    	public float calculateToFloat(int index) {
        	return index + 1.0f;
        }

    	@Override
		public String calculateToDisplayableString(int index) {
    		DecimalFormat df = new DecimalFormat("0");
			return df.format(calculateToFloat(index));
		}
    };
    
    public static Mapper MAX_RADIUS_MAPPER = new Mapper() {
    	@Override
    	public int calculateToInt(int index) {
    		throw new UnsupportedOperationException("There's a mismatch between the various data types in the properties mapper.");
        }

    	@Override
    	public float calculateToFloat(int index) {
        	return index + 1.0f;
        }

    	@Override
		public String calculateToDisplayableString(int index) {
    		DecimalFormat df = new DecimalFormat("0");
			return df.format(calculateToFloat(index));
		}
    };
    
    public static Mapper MIN_OUTLINE_WIDTH_MAPPER = new Mapper() {
    	@Override
    	public int calculateToInt(int index) {
    		throw new UnsupportedOperationException("There's a mismatch between the various data types in the properties mapper.");
        }

    	@Override
    	public float calculateToFloat(int index) {
        	return index / 2.0f;
        }

    	@Override
		public String calculateToDisplayableString(int index) {
    		DecimalFormat df = new DecimalFormat("0.0");
			return df.format(calculateToFloat(index));
		}
    };
    
    public static Mapper MAX_OUTLINE_WIDTH_MAPPER = new Mapper() {
    	@Override
    	public int calculateToInt(int index) {
        	throw new UnsupportedOperationException("There's a mismatch between the various data types in the properties mapper.");
        }

    	@Override
    	public float calculateToFloat(int index) {
        	return index / 2.0f;
        }

    	@Override
		public String calculateToDisplayableString(int index) {
    		DecimalFormat df = new DecimalFormat("0.0");
			return df.format(calculateToFloat(index));
		}
    };
    
    public static Mapper MIN_TRANSPARENCY_MAPPER = new Mapper() {
    	@Override
    	public int calculateToInt(int index) {
        	return index;
        }

    	@Override
    	public float calculateToFloat(int index) {
        	throw new UnsupportedOperationException("There's a mismatch between the various data types in the properties mapper.");
        }

    	@Override
		public String calculateToDisplayableString(int index) {
			return Integer.toString(calculateToInt(index));
		}
    };
    
    public static Mapper MAX_TRANSPARENCY_MAPPER = new Mapper() {
    	@Override
    	public int calculateToInt(int index) {
        	return index;
        }

    	@Override
    	public float calculateToFloat(int index) {
        	throw new UnsupportedOperationException("There's a mismatch between the various data types in the properties mapper.");
        }

    	@Override
		public String calculateToDisplayableString(int index) {
			return Integer.toString(calculateToInt(index));
		}
    };
    
    public static Mapper MIN_SPEED_MAPPER = new Mapper() {
    	@Override
    	public int calculateToInt(int index) {
        	throw new UnsupportedOperationException("There's a mismatch between the various data types in the properties mapper.");
        }

    	@Override
    	public float calculateToFloat(int index) {
        	return index / 10.0f;
        }

    	@Override
		public String calculateToDisplayableString(int index) {
    		DecimalFormat df = new DecimalFormat("0.00");
			return df.format(calculateToFloat(index));
		}
    };
    
    public static Mapper MAX_SPEED_MAPPER = new Mapper() {
    	@Override
    	public int calculateToInt(int index) {
        	throw new UnsupportedOperationException("There's a mismatch between the various data types in the properties mapper.");
        }

    	@Override
    	public float calculateToFloat(int index) {
        	return index / 10.0f;
        }

    	@Override
		public String calculateToDisplayableString(int index) {
    		DecimalFormat df = new DecimalFormat("0.00");
			return df.format(calculateToFloat(index));
		}
    };
    
    public static Mapper BRIGHTNESS_MAPPER = new Mapper() {
    	@Override
    	public int calculateToInt(int index) {
        	throw new UnsupportedOperationException("There's a mismatch between the various data types in the properties mapper.");
        }

    	@Override
    	public float calculateToFloat(int index) {
        	return (float)index / (float)BRIGHTNESS_MAX_INDEX;
        }

    	@Override
		public String calculateToDisplayableString(int index) {
    		DecimalFormat df = new DecimalFormat("0.00");
			return df.format(calculateToFloat(index));
		}
    };
    
    public static Mapper FRAME_RATE_MAPPER = new Mapper() {
    	@Override
    	public int calculateToInt(int index) {
        	return index + 1;
        }

    	@Override
    	public float calculateToFloat(int index) {
        	throw new UnsupportedOperationException("There's a mismatch between the various data types in the properties mapper.");
        }

    	@Override
		public String calculateToDisplayableString(int index) {
			return Integer.toString(calculateToInt(index));
		}
    };
    
    private static int getFrameInterval(int frameRate) {
    	return (int)Math.round(1000.0f / (float)frameRate);
    }
    
    class BokehEngine extends Engine implements OnSharedPreferenceChangeListener {
        private int BOKEH_COUNT = BOKEH_COUNT_MAPPER.calculateToInt(DEFAULT_BOKEH_COUNT);
        private int EFFECT_TYPE = EFFECT_TYPE_MAPPER.calculateToInt(DEFAULT_EFFECT_TYPE);
    	private float MIN_RADIUS = MIN_RADIUS_MAPPER.calculateToFloat(DEFAULT_MIN_RADIUS);
    	private float MAX_RADIUS = MAX_RADIUS_MAPPER.calculateToFloat(DEFAULT_MAX_RADIUS);
    	private float MIN_OUTLINE_WIDTH = MIN_OUTLINE_WIDTH_MAPPER.calculateToFloat(DEFAULT_MIN_OUTLINE_WIDTH);
    	private float MAX_OUTLINE_WIDTH = MAX_OUTLINE_WIDTH_MAPPER.calculateToFloat(DEFAULT_MAX_OUTLINE_WIDTH);
    	private int MIN_TRANSPARENCY = MIN_TRANSPARENCY_MAPPER.calculateToInt(DEFAULT_MIN_TRANSPARENCY);
    	private int MAX_TRANSPARENCY = MAX_TRANSPARENCY_MAPPER.calculateToInt(DEFAULT_MAX_TRANSPARENCY);
    	private float MIN_SPEED = MIN_SPEED_MAPPER.calculateToFloat(DEFAULT_MIN_SPEED);
    	private float MAX_SPEED = MAX_SPEED_MAPPER.calculateToFloat(DEFAULT_MAX_SPEED);
    	private float BRIGHTNESS = BRIGHTNESS_MAPPER.calculateToFloat(DEFAULT_BRIGHTNESS);
    	private long FRAME_INTERVAL = getFrameInterval(FRAME_RATE_MAPPER.calculateToInt(DEFAULT_FRAME_RATE));
    	private boolean SLIGHT_BLUR = DEFAULT_SLIGHT_BLUR;
        private boolean RADIATING_WAVES = DEFAULT_RADIATING_WAVES;
    	
		private final Paint paint3 = new Paint();
		private final Paint paint2 = new Paint();
        private final Paint mPaint = new Paint();
        private float mTouchX = -1;
        private float mTouchY = -1;
        private float mWidth;
        private float mHeight;
        
    	private IntBuffer canvasBuf;
    	private IntBuffer bokehBuf;
        
        private int cycleCounter = 0;

        private final Runnable mPainter = new Runnable() {
            public void run() {
                drawFrame();
                mutate();
          }
        };
        private boolean mVisible;
        private SharedPreferences mPrefs;

        BokehEngine() {
//    		paint3.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
    		paint3.setAntiAlias(true);

    		paint2.setAntiAlias(true);
    		paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
    		
            final Paint paint = mPaint;
            paint.setColor(0xffffffff);
            paint.setAntiAlias(true);
            paint.setStrokeWidth(2);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStyle(Paint.Style.STROKE);

            mPrefs = BokehWallpaper.this.getSharedPreferences(SHARED_PREFS_NAME, 0);
            mPrefs.registerOnSharedPreferenceChangeListener(this);
            onSharedPreferenceChanged(mPrefs, null);

        	fixFlippedMinMax();
        }

        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        	BOKEH_COUNT = BOKEH_COUNT_MAPPER.calculateToInt(Integer.parseInt((prefs.getString(
        						BokehWallpaperSettings.BOKEH_COUNT_KEY, Integer.toString(DEFAULT_BOKEH_COUNT)))));
        	EFFECT_TYPE = EFFECT_TYPE_MAPPER.calculateToInt(Integer.parseInt((prefs.getString(
					BokehWallpaperSettings.EFFECT_TYPE_KEY, Integer.toString(DEFAULT_EFFECT_TYPE)))));
        	MIN_RADIUS = MIN_RADIUS_MAPPER.calculateToFloat(Integer.parseInt(prefs.getString(
        						BokehWallpaperSettings.MIN_RADIUS_KEY, Integer.toString(DEFAULT_MIN_RADIUS))));
        	MAX_RADIUS = MAX_RADIUS_MAPPER.calculateToFloat(Integer.parseInt(prefs.getString(
        						BokehWallpaperSettings.MAX_RADIUS_KEY, Integer.toString(DEFAULT_MAX_RADIUS))));
        	MIN_OUTLINE_WIDTH = MIN_OUTLINE_WIDTH_MAPPER.calculateToFloat(Integer.parseInt(prefs.getString(
        						BokehWallpaperSettings.MIN_OUTLINE_WIDTH_KEY, Integer.toString(DEFAULT_MIN_OUTLINE_WIDTH))));
        	MAX_OUTLINE_WIDTH = MAX_OUTLINE_WIDTH_MAPPER.calculateToFloat(Integer.parseInt(prefs.getString(
        						BokehWallpaperSettings.MAX_OUTLINE_WIDTH_KEY, Integer.toString(DEFAULT_MAX_OUTLINE_WIDTH))));
        	MIN_TRANSPARENCY = MIN_TRANSPARENCY_MAPPER.calculateToInt(Integer.parseInt(prefs.getString(
        						BokehWallpaperSettings.MIN_TRANSPARENCY_KEY, Integer.toString(DEFAULT_MIN_TRANSPARENCY))));
        	MAX_TRANSPARENCY = MAX_TRANSPARENCY_MAPPER.calculateToInt(Integer.parseInt(prefs.getString(
        						BokehWallpaperSettings.MAX_TRANSPARENCY_KEY, Integer.toString(DEFAULT_MAX_TRANSPARENCY))));
        	MIN_SPEED = MIN_SPEED_MAPPER.calculateToFloat(Integer.parseInt(prefs.getString(
        						BokehWallpaperSettings.MIN_SPEED_KEY, Integer.toString(DEFAULT_MIN_SPEED))));
        	MAX_SPEED = MAX_SPEED_MAPPER.calculateToFloat(Integer.parseInt(prefs.getString(
        						BokehWallpaperSettings.MAX_SPEED_KEY, Integer.toString(DEFAULT_MAX_SPEED))));
        	BRIGHTNESS = BRIGHTNESS_MAPPER.calculateToFloat(Integer.parseInt(prefs.getString(
        						BokehWallpaperSettings.BRIGHTNESS_KEY, Integer.toString(DEFAULT_BRIGHTNESS))));
        	FRAME_INTERVAL = getFrameInterval(FRAME_RATE_MAPPER.calculateToInt(Integer.parseInt(prefs.getString(
        						BokehWallpaperSettings.FRAME_RATE_KEY, Integer.toString(DEFAULT_FRAME_RATE)))));
        	SLIGHT_BLUR = prefs.getBoolean(BokehWallpaperSettings.SLIGHT_BLUR_KEY, DEFAULT_SLIGHT_BLUR);
        	RADIATING_WAVES = prefs.getBoolean(BokehWallpaperSettings.RADIATING_WAVES_KEY, DEFAULT_RADIATING_WAVES);
        	fixFlippedMinMax();
            createBokehs();
        }
        
        private void fixFlippedMinMax() {
        	if (MIN_RADIUS > MAX_RADIUS) {
        		MAX_RADIUS = MIN_RADIUS;
        	}
        	if (MIN_OUTLINE_WIDTH > MAX_OUTLINE_WIDTH) {
        		MAX_OUTLINE_WIDTH = MIN_OUTLINE_WIDTH;
        	}
        	if (MIN_TRANSPARENCY > MAX_TRANSPARENCY) {
        		MAX_TRANSPARENCY = MIN_TRANSPARENCY;
        	}
        	if (MIN_SPEED > MAX_SPEED) {
        		MAX_SPEED = MIN_SPEED;
        	}
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            // By default we don't get touch events, so enable them.
            setTouchEventsEnabled(true);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mHandler.removeCallbacks(mPainter);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) {
                drawFrame();
                mutate();
            } else {
                mHandler.removeCallbacks(mPainter);
            }
        }

    	private void mutate() {
    		cycleCounter++;
    		if (cycleCounter >= 64) {
    			cycleCounter = 0;
    		}
    		
    		int count = bokehs.size();
    		Bokeh bokeh;
    		for (int i=0; i<count; i++) {
    			bokeh = bokehs.get(i);

    			bokeh.x = bokeh.x + bokeh.xMutation;
    			if (
    				(bokeh.xMutation > 0.0f && bokeh.x > mWidth) 
    				|| 
    				(bokeh.xMutation < 0.0f && bokeh.x < 0.0f)) 
    			{
    				bokeh.xMutation = -bokeh.xMutation;
        			bokeh.x = bokeh.x + bokeh.xMutation;
        			bokeh.x = bokeh.x + bokeh.xMutation;
    			}

    			bokeh.y = bokeh.y + bokeh.yMutation;
    			if (
    				(bokeh.yMutation > 0.0f && bokeh.y > mHeight) 
    				|| 
    				(bokeh.yMutation < 0.0f && bokeh.y < 0.0f)) 
    			{
    				bokeh.yMutation = -bokeh.yMutation;
        			bokeh.y = bokeh.y + bokeh.yMutation;
        			bokeh.y = bokeh.y + bokeh.yMutation;
    			}

    			bokeh.radius = bokeh.radius + bokeh.radiusMutation;
    			if (
    				(bokeh.radiusMutation > 0.0f && bokeh.radius > MAX_RADIUS) 
    				|| 
    				(bokeh.radiusMutation < 0.0f && bokeh.radius < MIN_RADIUS)) 
    			{
    				bokeh.radiusMutation = -bokeh.radiusMutation;
        			bokeh.radius = bokeh.radius + bokeh.radiusMutation;
        			bokeh.radius = bokeh.radius + bokeh.radiusMutation;
    			}

    			bokeh.alpha = bokeh.alpha + bokeh.alphaMutation;
    			if (
    				(bokeh.alphaMutation > 0 && bokeh.alpha > MAX_TRANSPARENCY) 
    				|| 
    				(bokeh.alphaMutation < 0 && bokeh.alpha < MIN_TRANSPARENCY)) 
    			{
    				bokeh.alphaMutation = -bokeh.alphaMutation;
        			bokeh.alpha = bokeh.alpha + bokeh.alphaMutation;
        			bokeh.alpha = bokeh.alpha + bokeh.alphaMutation;
    			}

    			populateBokeh(bokeh);
    		}
    	}

    	@Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            mWidth = width;
            mHeight = height;
            createBokehs();
            drawFrame();
            mutate();
        }
        
    	private void createBokehs() {
            Bokeh bokeh;
            bokehs.clear();
            for (int i=0; i<BOKEH_COUNT; i++) {
            	bokeh = createBokeh(0.0f + mWidth * random.nextFloat(), 0.0f + mHeight * random.nextFloat());
            	bokehs.add(bokeh);
            }
    	}
    	
    	private Bokeh createBokeh(float x, float y) {
            Bokeh bokeh = new Bokeh();
            bokeh.radius = MIN_RADIUS + (MAX_RADIUS - MIN_RADIUS) * random.nextFloat();
            bokeh.strokeWidth = MIN_OUTLINE_WIDTH + (MAX_OUTLINE_WIDTH - MIN_OUTLINE_WIDTH) * random.nextFloat();
            bokeh.x = x;
            bokeh.y = y;
            bokeh.alpha = MIN_TRANSPARENCY + random.nextInt(MAX_TRANSPARENCY - MIN_TRANSPARENCY + 1);
            bokeh.xMutation = (MAX_SPEED - MIN_SPEED) * (2.0f * random.nextFloat() - 1.0f);
            bokeh.yMutation = (MAX_SPEED - MIN_SPEED) * (2.0f * random.nextFloat() - 1.0f);
            bokeh.xMutation = bokeh.xMutation + Math.signum(bokeh.xMutation) * MIN_SPEED;
            bokeh.yMutation = bokeh.yMutation + Math.signum(bokeh.yMutation) * MIN_SPEED;
            bokeh.alphaMutation = 1 + random.nextInt(2);
            bokeh.radiusMutation = -0.7f + 1.4f * random.nextFloat();
            bokeh.paintStroke1 = new Paint();
            bokeh.paintStroke1.setStyle(Paint.Style.STROKE);
            bokeh.paintStroke1.setAntiAlias(true);
            if (EFFECT_TYPE == 1) {
                bokeh.paintStroke1.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
            }
            else if (EFFECT_TYPE == 2) {
                bokeh.paintStroke1.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
                if (RADIATING_WAVES) {
                    bokeh.paintStroke2 = new Paint();
                    bokeh.paintStroke2.setStyle(Paint.Style.STROKE);
                    bokeh.paintStroke2.setAntiAlias(true);
                	bokeh.paintStroke2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
                }
            }
            bokeh.paintFill = new Paint();
            bokeh.paintFill.setStyle(Paint.Style.FILL);
            bokeh.paintFill.setAntiAlias(true);
            if (EFFECT_TYPE == 1) {
            	bokeh.paintFill.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
            }
            else if (EFFECT_TYPE == 2) {
            	bokeh.paintFill.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
            }
            populateBokeh(bokeh);
            return bokeh;
    	}
    	
        private void populateBokeh(Bokeh bokeh) {
            float radiusAndStroke = bokeh.radius + bokeh.strokeWidth;
            float boundedWidth = mWidth - radiusAndStroke * 2.0f;
            float boundedHeight = mHeight - radiusAndStroke * 2.0f;
            float boundedHalfWidth = boundedWidth / 2.0f;
            float boundedHalfHeight = boundedHeight / 2.0f;
            float max = boundedHalfWidth * boundedHalfHeight;
            float xy = (bokeh.x - radiusAndStroke - boundedHalfWidth) * 
            			(bokeh.y - radiusAndStroke - boundedHalfHeight) + max;
            max = max * 2.0f;
            
            if (EFFECT_TYPE == 0 || EFFECT_TYPE == 1 || !RADIATING_WAVES) {
                bokeh.strokeColor = Color.HSVToColor(bokeh.alpha, new float[]{360.0f * xy / max, 0.9f, BRIGHTNESS});
                bokeh.paintStroke1.setColor(bokeh.strokeColor);
                bokeh.paintStroke1.setStrokeWidth(bokeh.strokeWidth);
                bokeh.paintFill.setColor(bokeh.strokeColor);
                bokeh.paintFill.setStrokeWidth(bokeh.strokeWidth);
            }
            else {
                int alpha1 = (int)(bokeh.alpha * ((64 - cycleCounter) / 64.0f));
                int alpha2 = (int)(bokeh.alpha * (((32 + 64 - cycleCounter) % 64) / 64.0f));
                int color1 = Color.HSVToColor(alpha1, new float[]{360.0f * xy / max, 0.9f, BRIGHTNESS});
                int color2 = Color.HSVToColor(alpha2, new float[]{360.0f * xy / max, 0.9f, BRIGHTNESS});
                bokeh.strokeColor = color1;
                bokeh.paintStroke1.setColor(color1);
                bokeh.paintStroke2.setColor(color2);
                float strokeWidth1 = Math.min(bokeh.strokeWidth, 4.0f * bokeh.strokeWidth * cycleCounter / 64.0f);
                float strokeWidth2 = Math.min(bokeh.strokeWidth, 4.0f * bokeh.strokeWidth * ((32 + cycleCounter) % 64) / 64.0f);
                bokeh.paintStroke1.setStrokeWidth(strokeWidth1);
                bokeh.paintStroke2.setStrokeWidth(strokeWidth2);

                int alpha3 = Color.HSVToColor(bokeh.alpha, new float[]{360.0f * xy / max, 0.9f, BRIGHTNESS});
                bokeh.paintFill.setColor(alpha3);
                bokeh.paintFill.setStrokeWidth(bokeh.strokeWidth);
            }
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mVisible = false;
            mHandler.removeCallbacks(mPainter);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xStep, float yStep, int xPixels, int yPixels) {
            drawFrame();
        }

        /*
         * Store the position of the touch event so we can use it for drawing later
         */
        @Override
        public void onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                mTouchX = event.getX();
                mTouchY = event.getY();
                if (bokehs.size() > 0) {
                    bokehs.remove(0);
                    Bokeh bokeh = createBokeh(mTouchX, mTouchY);
                    bokehs.add(bokeh);
                }
            }
            else {
                mTouchX = -1;
                mTouchY = -1;
            }
            super.onTouchEvent(event);
        }

        /*
         * Draw one frame of the animation. This method gets called repeatedly
         * by posting a delayed Runnable. You can do any drawing you want in
         * here.
         */
        void drawFrame() {
            final SurfaceHolder holder = getSurfaceHolder();

            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    // draw something
//                	drawBokeh3(c);
//                	drawBokeh2(c);
                	drawBokeh(c);
//                    drawTouchPoint(c);
                }
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c);
            }

            // Reschedule the next redraw
            mHandler.removeCallbacks(mPainter);
            if (mVisible) {
                mHandler.postDelayed(mPainter, FRAME_INTERVAL);
            }
        }
        void drawBokeh3(Canvas c) {
        	Config config = Config.ARGB_8888;
        	Options options = new Options();
        	options.inPreferredConfig = config;
        	Bitmap bokehBmp = BitmapFactory.decodeResource(getResources(), R.drawable.bokeh_outline_small, options);
        	if (canvasBuf == null) {
        		canvasBuf = IntBuffer.allocate(c.getWidth() * c.getHeight() * 4); // TODO: WTF? *4?
        		bokehBuf = IntBuffer.allocate(bokehBmp.getWidth() * bokehBmp.getHeight());
        	}
        	canvasBuf.clear();
        	bokehBuf.clear();
        	
        	// ----- draw into the canvas buffer -----
        	
    		int canvasWidth = c.getWidth();
    		int canvasHeight = c.getHeight();
			for (int h=0; h<canvasHeight; h++) {
				for (int w=0; w<canvasWidth; w++) {
    				canvasBuf.put(Color.rgb(0x80, w * 255 / canvasWidth, h * 255 / canvasHeight));
    			}
			}

			// ---------------------------------------
			
//        	Bitmap bmm = Bitmap.createBitmap(canvasBuf.array(), c.getWidth(), c.getHeight(), Bitmap.Config.ARGB_8888);
        	Bitmap bmm = Bitmap.createBitmap(c.getWidth(), c.getHeight(), Bitmap.Config.ARGB_8888);
        	bmm.copyPixelsFromBuffer(canvasBuf);
//        	bokehBmp.copyPixelsToBuffer(bokehBuf);
        	BitmapDrawable bd = new BitmapDrawable(getResources(), bmm);
//        	Bitmap b = bd.getBitmap();
//        	b.
        	
    		Drawable drawable = getResources().getDrawable(R.drawable.bokeh_outline_small);
//    		Drawable drawable = getResources().getDrawable(R.drawable.bokeh_outline_tiny);
    		bd.setBounds(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());
//    		Bitmap b = Bitmap.createBitmap(c.getWidth(), c.getHeight(), Bitmap.Config.ARGB_8888);
//    		b.setPixels(thumbsData[state][thumbDirection], 0, width, 0, 0, width, height);
        	c.save();

            c.drawColor(0xfff8e0f8);
    		int count = bokehs.size();
    		Bokeh bokeh;
    		Paint paint = new Paint();
    		paint.setAntiAlias(true);
    		int widthCount = bokehBmp.getWidth();
    		int heightCount = bokehBmp.getHeight();
			bd.setAntiAlias(true);
//    		for (int i=0; i<count; i++) {
//    			bokeh = bokehs.get(i);
//        		c.translate(bokeh.x, bokeh.y);
//        		c.drawBitmap(bokehBmp, 0, 0, paint);
//        		drawable.draw(c);
//    			bd.draw(c);
//        		c.translate(-bokeh.x, -bokeh.y);
//    			for (int w=0; w<widthCount; w++) {
//        			for (int h=0; h<heightCount; h++) {
//        				
//        			}
//    			}
//            }
			bd.draw(c);
        	bmm.recycle();
        	c.restore();
        }
        
        void drawBokeh2(Canvas c) {
        	Config config = Config.ARGB_8888;
        	Options options = new Options();
        	options.inPreferredConfig = config;
        	Bitmap bokehBmp = BitmapFactory.decodeResource(getResources(), R.drawable.bokeh_outline_small, options);

        	c.save();

            c.drawColor(0xff200030);
    		int count = bokehs.size();
    		Bokeh bokeh;
    		for (int i=0; i<count; i++) {
                int sc = c.saveLayer(0, 0, c.getWidth(), c.getHeight(), null,
                        Canvas.MATRIX_SAVE_FLAG |
                        Canvas.CLIP_SAVE_FLAG |
                        Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                        Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                        Canvas.CLIP_TO_LAYER_SAVE_FLAG);
    			bokeh = bokehs.get(i);
        		c.translate(bokeh.x, bokeh.y);
        		c.drawBitmap(bokehBmp, 0.0f, 0.0f, paint2);
        		c.translate(-bokeh.x, -bokeh.y);
        		c.restoreToCount(sc);
            }
        	c.restore();
        }

        void drawBokeh(Canvas c) {
        	c.save();

            c.drawColor(0xff000000);
    		int count = bokehs.size();
    		Bokeh bokeh;

    		for (int i=0; i<count; i++) {
    			bokeh = bokehs.get(i);
    			if (EFFECT_TYPE == 0) {
    				c.drawCircle(bokeh.x, bokeh.y, bokeh.radius + bokeh.strokeWidth / 2.0f, bokeh.paintFill);
                    c.drawCircle(bokeh.x, bokeh.y, bokeh.radius, bokeh.paintStroke1);
    			}
    			else if (EFFECT_TYPE == 1) {
                    if (SLIGHT_BLUR) {
                        c.drawCircle(bokeh.x, bokeh.y, bokeh.radius + bokeh.strokeWidth / 2.0f + 2.0f, bokeh.paintFill);
                    }
                    else {
                        c.drawCircle(bokeh.x, bokeh.y, bokeh.radius + bokeh.strokeWidth / 2.0f, bokeh.paintFill);
                    }
                    c.drawCircle(bokeh.x, bokeh.y, bokeh.radius, bokeh.paintStroke1);
    			}
    			else if (EFFECT_TYPE == 2) {
                    c.drawCircle(bokeh.x, bokeh.y, bokeh.radius, bokeh.paintFill);
                    if (RADIATING_WAVES) {
                    	float max1 = 2.0f * bokeh.strokeWidth * cycleCounter / 64.0f;
                    	float max3 = 2.0f * bokeh.strokeWidth * ((32 + cycleCounter) % 64) / 64.0f;
                    	float max2 = bokeh.strokeWidth * (-0.5f + 4.0f * cycleCounter / 64.0f);
                    	float max4 = bokeh.strokeWidth * (-0.5f + 4.0f * ((32 + cycleCounter) % 64) / 64.0f);
                    	c.drawCircle(bokeh.x, bokeh.y, bokeh.radius + Math.max(max1, max2), bokeh.paintStroke1);
                    	c.drawCircle(bokeh.x, bokeh.y, bokeh.radius + Math.max(max3, max4), bokeh.paintStroke2);
                    }
                    else {
                        c.drawCircle(bokeh.x, bokeh.y, bokeh.radius + bokeh.strokeWidth * 1.5f, bokeh.paintStroke1);
                    }
    			}
            }
        	
        	c.restore();
        }
    }

    public static class Bokeh {
    	public int strokeColor;
    	public int alpha;
    	public float strokeWidth;
    	public float x;
    	public float y;
    	public float radius;
    	public Paint paintStroke1;
    	public Paint paintStroke2;
    	public Paint paintFill;
    	public float xMutation;
    	public float yMutation;
    	public int alphaMutation;
    	public float radiusMutation;
    	public float strokeMutation;
    }
}
