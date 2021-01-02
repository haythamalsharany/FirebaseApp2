package com.alsharany.firebaseapp

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class InputDialogFragmen : DialogFragment() {
    lateinit var titleEditeText:EditText
    lateinit var detailsEditText:EditText
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val v = activity?.layoutInflater?.inflate(R.layout.add_news_dialog, null)
         titleEditeText = v?.findViewById(R.id.news_title_ed) as EditText
         detailsEditText = v?.findViewById(R.id.news_details_ed) as EditText



        return AlertDialog.Builder(requireContext(), R.style.ThemeOverlay_AppCompat_Dialog_Alert)
            .setView(v)
            .setTitle("add new news")
            .setPositiveButton("save") { dialog, _ ->
                if(validateInput(titleEditeText.text.toString())==true){
                    if (validateInput( detailsEditText.text.toString())==true){

                        val news = NewsModel(

                            titleEditeText.text.toString(),
                            detailsEditText.text.toString(),

                            ).toMap()
                        targetFragment?.let {
                            (it as AddNews).onAddNews(news)
                        }


                    }
                    else
                        {

                            detailsEditText.apply {
                                hint="invalid Title"
                                setHintTextColor(Color.RED)
                            }
                        }

                }
                else
                    titleEditeText.apply {
                        hint="invalid Details"
                        setHintTextColor(Color.RED)
                    }





            }
            .setNegativeButton("cancel") { dialog, _ ->
                dialog.cancel()

            }.create()
    }
   fun validateInput(text:String) =
       !(text.isNullOrBlank()||text.isNullOrEmpty())





    companion object {
        fun newInstance(): InputDialogFragmen {

            return InputDialogFragmen()
        }
    }
    interface AddNews{
        fun onAddNews(news:MutableMap<String,Any>)
    }

}