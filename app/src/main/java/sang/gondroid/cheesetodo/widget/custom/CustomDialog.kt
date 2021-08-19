package sang.gondroid.cheesetodo.widget.custom

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import sang.gondroid.cheesetodo.databinding.LayoutCustomDialogBinding

class CustomDialog(
    context: Context,
    private val dialogTitleText : String,
    private val descriptionText : String,
    private val customDialogClickListener: CustomDialogClickListener
) : Dialog(context) {

    private val binding by lazy {
        LayoutCustomDialogBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            dialogTitleTextView.text = dialogTitleText
            titleDescriptionTextView.text = descriptionText

            dialogNegative.setOnClickListener {
                customDialogClickListener.onNegativeClick()
                dismiss()
            }

            dialogPositive.setOnClickListener {
                customDialogClickListener.onPositiveClick()
                dismiss()
            }
        }

        setCanceledOnTouchOutside(false)
        setCancelable(true)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }
}