package io.ninja

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import org.jsoup.Jsoup
import io.ninja.Item
import java.io.File
import java.time.LocalDateTime

// Get french month
fun generateMonth(month: String): String {
    when (month) {
        "01" -> return "Janvier"
        "02" -> return "Fevrier"
        "03" -> return "Mars"
        "04" -> return "Avril"
        "05" -> return "Mai"
        "06" -> return "Juin"
        "07" -> return "Juillet"
        "08" -> return "Aout"
        "09" -> return "Septembre"
        "10" -> return "Octobre"
        "11" -> return "Novembre"
        "12" -> return "Decembre"
        else -> return "None"
    }
}

fun main(args: Array<String>) {
    println("Start current day parsing: ${LocalDateTime.now()}")

    val parser = Parser()

    var currentIndex = ""
    var jtTime="le-13h"
    if (args.size >= 1) {
        jtTime = args[0]
    }

    if (args.size >= 2) {
        currentIndex = args[1]
    }
    val result = ArrayList<String>()

    // Choose a jt on LCI page

    //for (i in 2..10) {

    val i = ""
    // Load first page data of a LCI jt
    Jsoup.connect("https://www.lci.fr/emission/${jtTime}/" + i).get().run {
        select("a").forEachIndexed { _, element ->
            if (element.attr("class") == "medium-3col-article-block-article-link") {

                // Load current HREF page and current element id
                val currentHref = element.attr("href")
                val currentId = currentHref.split("-").last().removeSuffix(".html")
                val localHref="https://www.lci.fr${currentHref}"

                //println(currentId)
                // Then connect to specific page element
                Jsoup.connect(localHref).get().run {

                    // Load it's details
                    select("script").forEachIndexed { _, element ->
                        if (element.attr("type") == "application/ld+json") {
                            if (element.data().contains("uploadDate")) {

                                // Generate String builder
                                val sb = StringBuilder()
                                sb.append(element.data())

                                // And load existing JSON data
                                val json: JsonObject = parser.parse(sb) as JsonObject

                                // Apply specific treatment to get category from JSON
                                var category = "None"
                                if (json.containsKey("contentUrl")) {
                                    category = json.get("contentUrl").toString().split("/").get(1)
                                }

                                // Save result as CSV line saving: uploadDate, name, description, category
                                result.add("${json.get("uploadDate")}, " +
                                        "${json.get("name").toString().replace(",","").replace("\n", "").replace("\"","")}, " +
                                        "${json.get("description").toString().replace(",","").replace("\n", "").replace("\"","")}, " +
                                        "$category, " +
                                        "$currentId, " +
                                        "$localHref"
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    //}

    // Current bulk buffer
    val sb = StringBuilder()

    result.forEach {
        val items = it.split(",")

        // Remove item with empty date
        if (items[0] != "") {
            val numberMonth = items[0].split("-")[1]

            // Get current month in french
            val month: String = generateMonth(numberMonth)

            // Remove blank from current id
            val currentId: Number = items[4].trim().toInt()

            // Generate an item
            val item = Item(items[0], items[1], items[2], items[3], month, currentIndex, currentId, items[5], jtTime)

            // Add item to current bulk
            sb.append(item.toString())
            sb.append("\n")
        }
    }

    println(sb.length)
    // Write current bulk in a file
    val bulk = File("bulk_ninja")
    bulk.writeText(sb.toString())

    println("Successfully end parsing of ${jtTime}, at ${LocalDateTime.now()}")
}