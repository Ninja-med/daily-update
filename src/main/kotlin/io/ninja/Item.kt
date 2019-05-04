package io.ninja

class Item(val myDate: String, val titre: String, val description: String, val category: String, val month: String, val index: String, val myId: Number, val href: String, val source: String) {

    override fun toString(): String {
        return """{"index": {"_type": "doc", "_id": ${myId.toString()}, "_index": "${index}"}}
{"date": "${myDate}", "titre": "${titre}", "description": "${description}",  "category": "${category}", "month": "${month}", "href": "${href}", "source":"${source}"}"""
    }
}