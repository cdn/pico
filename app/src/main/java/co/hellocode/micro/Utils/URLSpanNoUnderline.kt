package co.hellocode.micro.Utils


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.design.widget.Snackbar
import android.text.Html
import android.text.Spannable
import android.text.Spanned
import android.text.TextPaint
import android.text.style.URLSpan
import android.view.View
import co.hellocode.micro.R


/**
 * Created by Josh on 30/11/2015.
 */
class URLSpanNoUnderline(p_Url: String, context: Context) : URLSpan(p_Url) {
    internal var url: String
    internal val context: Context

    init {
        var p_Url = p_Url

        if (p_Url.startsWith("/")) {
            p_Url = "https://micro.blog$p_Url"
        }
        this.url = p_Url
        this.context = context
    }

    override fun onClick(widget: View) {
        val urlText = Html.fromHtml("<font color='#ffffff'>" + this.getURL() + "</font>")
        val url = this.url
//        Snackbar.make(widget, urlText, Snackbar.LENGTH_LONG).setAction(
//                "Open") {
//            val i = Intent(Intent.ACTION_VIEW)
//            i.data = Uri.parse(url)
//            widget.context.startActivity(i)
//        }.setActionTextColor(widget.resources.getColor(R.color.colorAccent)).show()
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        widget.context.startActivity(i)

        //super.onClick(widget);
    }

    override fun updateDrawState(p_DrawState: TextPaint) {
        super.updateDrawState(p_DrawState)
        p_DrawState.isUnderlineText = false
        p_DrawState.color = context.resources.getColor(R.color.colorAccent)
    }

    companion object {

        fun removeUnderlines(p_Text: Spannable, context: Context): Spannable {
            val spans = p_Text.getSpans(0, p_Text.length, URLSpan::class.java)

            for (span in spans) {

                val start = p_Text.getSpanStart(span)
                val end = p_Text.getSpanEnd(span)
                p_Text.removeSpan(span)
                val newSpan = URLSpanNoUnderline(span.url, context)
                p_Text.setSpan(newSpan, start, end, 0)
            }
            return p_Text
        }
    }


}