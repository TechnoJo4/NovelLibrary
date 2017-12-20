package io.github.gmathi.novellibrary.network

import io.github.gmathi.novellibrary.dataCenter
import io.github.gmathi.novellibrary.network.HostNames.USER_AGENT
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.regex.Pattern
import javax.net.ssl.SSLPeerUnverifiedException


class NovelApi {

    companion object {
        var cookies: String? = ""
        var cookiesMap: HashMap<String, String>? = HashMap()
    }

    fun getDocument(url: String): Document {
        try {

//            val antibot = OkHttpAntiAntibotCloudFlareFactory().createInstance()
//            val html = antibot.getUrl(url)
//            antibot.close() //not really necessary in this case, since it does nothing
//            return Jsoup.parse(html)

//            val cf = CloudFlare(url)
//            cf.setUA(USER_AGENT)
//            val cookiesMap = cf.List2Map(cf.cookiesMap())

            return Jsoup
                .connect(url)
                .cookies(cookiesMap)
                .referrer(url)
                .ignoreHttpErrors(true)
                .timeout(30000)
                .get()
        } catch (e: SSLPeerUnverifiedException) {
            val p = Pattern.compile("Hostname\\s(.*?)\\snot", Pattern.DOTALL or Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE or Pattern.MULTILINE) // Regex for the value of the key
            val m = p.matcher(e.localizedMessage)
            if (m.find()) {
                val hostName = m.group(1)
                if (!HostNames.isVerifiedHost(hostName)) {
                    dataCenter.saveVerifiedHost(m.group(1))
                    return getDocument(url)
                }
            }
            throw e
        }
    }

    fun getDocumentWithUserAgent(url: String, canLoop: Boolean = true): Document {
        try {
            val doc = Jsoup
                .connect(url)
                .referrer(url)
                .cookies(cookiesMap)
                .ignoreHttpErrors(true)
                .timeout(30000)
                .userAgent(USER_AGENT)
                .get()

            if (canLoop && doc != null && doc.location().contains("rssbook") && doc.location().contains(HostNames.QIDIAN)) {
                return getDocumentWithUserAgent(doc.location().replace("rssbook", "book"), false)
            }

            return doc

        } catch (e: SSLPeerUnverifiedException) {
            val p = Pattern.compile("Hostname\\s(.*?)\\snot", Pattern.DOTALL or Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE or Pattern.MULTILINE) // Regex for the value of the key
            val m = p.matcher(e.localizedMessage)
            if (m.find()) {
                val hostName = m.group(1)
                if (!HostNames.isVerifiedHost(hostName)) {
                    dataCenter.saveVerifiedHost(m.group(1))
                    return getDocumentWithUserAgent(url)
                }
            }
            throw e
        }
    }

    fun getDocumentWithUserAgentIgnoreContentType(url: String): Document {
        try {
            return Jsoup
                .connect(url)
                .referrer(url)
                .timeout(30000)
                .cookies(cookiesMap)
                .userAgent(USER_AGENT)
                .ignoreContentType(true)
                .get()
        } catch (e: SSLPeerUnverifiedException) {
            val p = Pattern.compile("Hostname\\s(.*?)\\snot", Pattern.DOTALL or Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE or Pattern.MULTILINE) // Regex for the value of the key
            val m = p.matcher(e.localizedMessage)
            if (m.find()) {
                val hostName = m.group(1)
                if (!HostNames.isVerifiedHost(hostName)) {
                    dataCenter.saveVerifiedHost(m.group(1))
                    return getDocumentWithUserAgent(url)
                }
            }
            throw e
        }
    }

}