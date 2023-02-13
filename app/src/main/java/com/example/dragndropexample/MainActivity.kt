package com.example.dragndropexample

import android.content.ClipData
import android.content.ClipDescription
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.example.dragndropexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val activityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)


        //LAST STEP IS TO ADD dragListener
        activityMainBinding.llTop.setOnDragListener(dragListener)
        activityMainBinding.llBottom.setOnDragListener(dragListener)


        //What happens when long hold finger on view:
        activityMainBinding.dragView.setOnLongClickListener {
            //we are going to create clip data. Attaching data to drag object
            val clipText = "This is our clip data text"
            //Convert this text to clip item.
            val item = ClipData.Item(clipText)
            //We have to create mimetypes(something like file endings or what kind of data we want to put into clip data)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            //1) Now create after that actual clip data
            val data = ClipData(clipText, mimeTypes, item)
            //2) Now we can create shadow builder ( to get a visual feedback for the object we are currently dragging)
            val dragShadowBuilder = View.DragShadowBuilder(it)

            //After that we can start our drag and drop (it - object we actually drag
            it.startDragAndDrop(data, dragShadowBuilder, it, 0)

            //Now we want to make our view invisible from the layout where it came from, and is only visible to the drag shadow
            it.visibility = View.INVISIBLE

            //return
            true
        }
    }


    //Now we need to create drag listener which we will attach to our LAYOUTS to be able to respond to our drag events:
    val dragListener = View.OnDragListener { view, event ->
        //Here we want to describe all actions supported by event
        when(event.action){
            DragEvent.ACTION_DRAG_STARTED -> {
                //We just want to return whether our drag object is able to accept our drag data
                event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }
            //Called when our drag view enters layouts boundaries, in that case we just want to update our layout view by writing:
            DragEvent.ACTION_DRAG_ENTERED -> {
                view.invalidate()
                true
            }
            //We don't need here , but still need to use in when expression, which is action drag location, but here is not important
            DragEvent.ACTION_DRAG_LOCATION -> true
            //We need it  as ACTION_DRAG_ENTERED, this event occures when the drag view leaves our layout boundaries
            DragEvent.ACTION_DRAG_EXITED -> {
                view.invalidate()
                true
            }
            //Most important part. When we drop our dragg view.
            DragEvent.ACTION_DROP -> {
                //First we want to get our clip data which we attached to our  drag view
                val item = event.clipData.getItemAt(0) //we have only one item
                //get text from item
                val dragData = item.text
                Toast.makeText(this, dragData, Toast.LENGTH_LONG).show()

                view.invalidate()

                //we want to remove view from the layout it was before and add it to a new layout
                val v = event.localState as View //it is our drag view
                val owner = v.parent as ViewGroup//(get the layout where view was before)
                owner.removeView(v) //remove prom previous layout
                val destination = view as LinearLayout
                destination.addView(v)
                v.visibility= View.VISIBLE
                true
            }
            //we want to invalidate our view here
            DragEvent.ACTION_DRAG_ENDED ->{
                view.invalidate()
                true
            }
            else -> false
        }
    }


}