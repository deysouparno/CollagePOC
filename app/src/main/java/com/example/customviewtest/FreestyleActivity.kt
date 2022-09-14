package com.example.customviewtest

import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.ui.AppBarConfiguration
import com.example.customviewtest.databinding.ActivityFreestyleBinding

class FreestyleActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityFreestyleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFreestyleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        for (i in 1..5) {
            val image = CustomImageView(this)
            binding.collageContainer.addView(image)
            image.layoutParams = ConstraintLayout.LayoutParams(dpToPx(200), dpToPx(200))
        }


    }

    private fun dpToPx(dim: Int) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dim.toFloat(),
        resources.displayMetrics
    ).toInt()

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_freestyle)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }
}