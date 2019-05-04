package io.ninja

import java.io.File
import java.io.InputStream

/*
 *  Manual csv files parser
 *  Expect 2 args folder and filename (without the .csv) and the month as 07 for july
 */

fun main(args: Array<String>) {


    val folder = args[0]
    val file = args[1]
    // Set each files parameter
    var id=0

    val filename = "bulk_" + file

    val month = args[2]

    val sb = StringBuffer()


    val inputStream: InputStream = File(folder + file + ".csv").inputStream()
    val lineList = mutableListOf<String>()

    inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it)} }

    // Save years
    val years = lineList.get(0).split(",")
    lineList.removeAt(0)

    // Reverse list to start with the first days of the month
    lineList.reverse()

    // Pre-load essential data
    val days = ArrayList<String>()

    years.forEach{
        days.add("0")
    }

    val month_string = generateMonth(month)
    val date = "2018-04-06T14:10:41.000Z"
    val endDate = "T12:00:00.000Z"


    // For each elements
    lineList.forEach{
        //println(days)

        val items = it.split(",")

        for (i in items.indices) {

            // Check items-date, and increase date if it a new day
            if (items[i].startsWith("Le 13 heures du ")) {

                // increment date - load current day
                val removeTitle = items[i].removePrefix("Le 13 heures du ")
                var day = removeTitle.split(" ").get(0).removeSuffix("er")

                if (day.length < 2) {
                    day = "0" + day
                }

                // Save day
                days[i] = day
            } else if (!days[i].equals("0")) {
                id++
                val item_date = "${years[i]}-$month-${days[i]}$endDate"

                val currentItem = Item(item_date, items[i].replace("\"", ""), "Manual upload", "manual", month_string, "ninja13h",id, "non", "le-13h")
                sb.append(currentItem.toString())
                sb.append("\n")
            }
            //println(items[i])
        }

        //println(">  " + it)
    }
    //  println(sb.toString())

    println(id)

    // Write current bulk in a file
    val bulk = File(filename)
    bulk.writeText(sb.toString())
}