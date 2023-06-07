package com.adrian.recycash.ui.profile.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.adrian.recycash.data.local.LinkedinProfile
import com.adrian.recycash.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val linkedinCard = listOf(
            LinkedinProfile(binding.cvAdrian, "https://www.linkedin.com/in/adrian-triputra/"),
            LinkedinProfile(binding.cvRizki, "https://www.linkedin.com/in/muhammad-rizki-adiyaksa-24a20314b/"),
            LinkedinProfile(binding.cvJidan, "https://www.linkedin.com/in/muhammad-zidan-k-22bb31130/"),
            LinkedinProfile(binding.cvDaffa, "https://www.linkedin.com/in/mochammad-akmalludin-daffa/"),
            LinkedinProfile(binding.cvAlvin, "https://www.linkedin.com/in/muhammad-alvin-hilmy-a08b6b19a/"),
            LinkedinProfile(binding.cvIntan, "https://www.linkedin.com/in/putriintanamaliadahlan/")
        )

        linkedinCard.forEach { linkedinProfile ->  
            linkedinProfile.view.setOnClickListener {
                openLink(linkedinProfile.url)
            }
        }
    }

    private fun openLink(url: String){
        val userIntent = Intent(Intent.ACTION_VIEW)
        userIntent.data = Uri.parse(url)

        if (userIntent.resolveActivity(packageManager) != null) {
            startActivity(userIntent)
        } else {
            Toast.makeText(this, "No web browser app found", Toast.LENGTH_SHORT).show()
        }
    }
}