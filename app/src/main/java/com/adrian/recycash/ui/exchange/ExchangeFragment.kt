package com.adrian.recycash.ui.exchange

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.adrian.recycash.R
import com.adrian.recycash.databinding.FragmentExchangeBinding

class ExchangeFragment : Fragment() {

    private var _binding: FragmentExchangeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExchangeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val imgGopay = binding.imgGopay
        val imgShopeepay = binding.imgShopeepay
        val imgOvo = binding.imgOvo
        val imgDana = binding.imgDana
        val imgLinkAja = binding.imgLinkaja
        val imgDoku = binding.imgDoku
        val imgIsaku = binding.imgIsaku

        val imgAlfagift = binding.imgAlfagift
        val imgKopken = binding.imgKopiKenangan
        val imgStarbucks = binding.imgStarbucks
        val imgPulsa = binding.imgPulsa

        val toastClickListener = View.OnClickListener {
            val message = when (it.id) {
                R.id.img_gopay -> getString(R.string.coming_soon)
                R.id.img_shopeepay -> getString(R.string.coming_soon)
                R.id.img_ovo -> getString(R.string.coming_soon)
                R.id.img_dana -> getString(R.string.coming_soon)
                R.id.img_linkaja -> getString(R.string.coming_soon)
                R.id.img_doku -> getString(R.string.coming_soon)
                R.id.img_isaku -> getString(R.string.coming_soon)
                R.id.img_alfagift -> getString(R.string.coming_soon)
                R.id.img_kopi_kenangan -> getString(R.string.coming_soon)
                R.id.img_starbucks -> getString(R.string.coming_soon)
                R.id.img_pulsa -> getString(R.string.coming_soon)
                else -> null
            }

            message?.let { msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        imgGopay.setOnClickListener(toastClickListener)
        imgShopeepay.setOnClickListener(toastClickListener)
        imgOvo.setOnClickListener(toastClickListener)
        imgDana.setOnClickListener(toastClickListener)
        imgLinkAja.setOnClickListener(toastClickListener)
        imgDoku.setOnClickListener(toastClickListener)
        imgIsaku.setOnClickListener(toastClickListener)
        imgAlfagift.setOnClickListener(toastClickListener)
        imgKopken.setOnClickListener(toastClickListener)
        imgStarbucks.setOnClickListener(toastClickListener)
        imgPulsa.setOnClickListener(toastClickListener)

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}