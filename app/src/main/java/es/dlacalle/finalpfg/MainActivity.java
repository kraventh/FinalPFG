package es.dlacalle.finalpfg;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;
import android.widget.ViewFlipper;


public class MainActivity extends Activity {

    private float x1, x2, lastX;
    static final int MIN_DISTANCE = 150;
    private ViewFlipper viewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
    }

    // Method to handle touch event like left to right swap and right to left swap
//    public boolean onTouchEvent(MotionEvent touchevent)
//    {
//        switch (touchevent.getAction())
//        {
//            // when user first touches the screen to swap
//            case MotionEvent.ACTION_DOWN:
//            {
//                lastX = touchevent.getX();
//                break;
//            }
//            case MotionEvent.ACTION_UP:
//            {
//                float currentX = touchevent.getX();
//
//                // if left to right swipe on screen
//                if (lastX < currentX && Math.abs(lastX-currentX)>150 )
//                {
//                    // If no more View/Child to flip
//                    if (viewFlipper.getDisplayedChild() == 0)
//                        break;
//
//                    // set the required Animation type to ViewFlipper
//                    // The Next screen will come in form Left and current Screen will go OUT from Right
//                    viewFlipper.setInAnimation(this, R.anim.in_from_left);
//                    viewFlipper.setOutAnimation(this, R.anim.out_to_right);
//                    // Show the next Screen
//                    viewFlipper.showNext();
//                }
//
//                // if right to left swipe on screen
//                if (lastX > currentX && Math.abs(lastX-currentX)>150)
//                {
//                    if (viewFlipper.getDisplayedChild() == 1)
//                        break;
//                    // set the required Animation type to ViewFlipper
//                    // The Next screen will come in form Right and current Screen will go OUT from Left
//                    viewFlipper.setInAnimation(this, R.anim.in_from_right);
//                    viewFlipper.setOutAnimation(this, R.anim.out_to_left);
//                    // Show The Previous Screen
//                    viewFlipper.showPrevious();
//                }
//                break;
//            }
//        }
//        return false;
//    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                x1 = event.getX();
//                break;
//            case MotionEvent.ACTION_UP:
//                x2 = event.getX();
//                float deltaX = x2 - x1;
//
//                if (Math.abs(deltaX) > MIN_DISTANCE) {
//                    // Left to Right swipe action
//                    if (x2 > x1) {
//                        Toast.makeText(this, "Left to Right swipe [Next]", Toast.LENGTH_SHORT).show();
//                    }
//
//                    // Right to left swipe action
//                    else {
//                        Toast.makeText(this, "Right to Left swipe [Previous]", Toast.LENGTH_SHORT).show();
//                    }
//
//                } else {
//                    // consider as something else - a screen tap for example
//                }
//                break;
//        }
//        return super.onTouchEvent(event);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
