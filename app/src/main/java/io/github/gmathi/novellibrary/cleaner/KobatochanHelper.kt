package io.github.gmathi.novellibrary.cleaner

import android.net.Uri
import io.github.gmathi.novellibrary.network.HostNames
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.File


class KobatochanHelper : HtmlHelper() {

    override fun additionalProcessing(doc: Document) {
        removeCSS(doc)
        var contentElement = doc.body().getElementsByTag("div").firstOrNull { it.hasClass("entry-content") }
        contentElement?.prepend("<h4>${getTitle(doc)}</h4><br>")

//        contentElement?.getElementsByTag("a")?.firstOrNull {
//            it.text().contains("Previous Chapter")
//                || it.text().contains("Next Chapter")
//                || it.text().contains("Project Page")
//        }?.parent()?.remove()

        do {
            contentElement?.siblingElements()?.remove()
            cleanClassAndIds(contentElement)
            contentElement = contentElement?.parent()
        } while (contentElement != null && contentElement.tagName() != "body")
        cleanClassAndIds(contentElement)
        doc.head().children().remove()
    }

    override fun downloadImage(element: Element, file: File): File? {
        val uri = Uri.parse(element.attr("src"))
        if (uri.toString().contains("uploads/avatars")) return null
        else return super.downloadImage(element, file)
    }

    override fun removeJS(doc: Document) {
        super.removeJS(doc)
        doc.getElementsByTag("noscript").remove()
    }

    override fun getLinkedChapters(doc: Document): ArrayList<String> {

        val links = ArrayList<String>()
        val contentElement = doc.body().getElementsByTag("div").firstOrNull { it.hasClass("entry-content") }
        val otherLinks = contentElement?.getElementsByAttributeValueContaining("href", HostNames.KOBATOCHAN)
        if (otherLinks != null && otherLinks.isNotEmpty()) {
            otherLinks.mapTo(links) { it.attr("href") }
        }
        return links
    }

    override fun toggleTheme(isDark: Boolean, doc: Document): Document {
        return super.toggleThemeDefault(isDark, doc)
    }


}
