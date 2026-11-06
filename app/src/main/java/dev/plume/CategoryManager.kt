package dev.plume

import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

data class Category(
    val id: String,
    val name: String,
    val apps: List<String> = emptyList(),
    val size: String = "inherit", // inherit, standard, large, huge
    val iconStyle: String = "inherit", // inherit, banner, mono, glyph, icon
    val belowWatch: Boolean = false,
    val icon: String = "auto" // auto, bookmark, film, tv, apps, gamepad, video, music, star, heart, folder, settings
)

object CategoryManager {
    const val PREF_CATEGORIES = "custom_categories"
    const val PREF_SHOW_TITLES = "category_show_titles"
    const val DEFAULT_ID = "default"

    fun loadCategories(prefs: SharedPreferences, defaultName: String, fallbackOrder: List<String>): List<Category> {
        val raw = prefs.getString(PREF_CATEGORIES, null)
        if (raw.isNullOrEmpty()) {
            val hidden = prefs.getStringSet("row_hidden", emptySet()) ?: emptySet()
            val initialApps = fallbackOrder.filter { it !in hidden }
            return listOf(Category(DEFAULT_ID, defaultName, initialApps))
        }
        return try {
            val array = JSONArray(raw)
            val list = mutableListOf<Category>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.optString("id", if (i == 0) DEFAULT_ID else "cat_$i")
                val name = obj.optString("name", defaultName)
                val appsArray = obj.optJSONArray("apps")
                val apps = mutableListOf<String>()
                if (appsArray != null) {
                    for (j in 0 until appsArray.length()) {
                        apps.add(appsArray.getString(j))
                    }
                }
                val size = obj.optString("size", "inherit")
                val iconStyle = obj.optString("iconStyle", "inherit")
                val belowWatch = obj.optBoolean("belowWatch", false)
                val icon = obj.optString("icon", "auto")
                list.add(Category(id, name, apps, size, iconStyle, belowWatch, icon))
            }
            if (list.isEmpty()) {
                listOf(Category(DEFAULT_ID, defaultName, fallbackOrder))
            } else {
                list
            }
        } catch (_: Exception) {
            listOf(Category(DEFAULT_ID, defaultName, fallbackOrder))
        }
    }

    fun saveCategories(prefs: SharedPreferences, categories: List<Category>) {
        val array = JSONArray()
        for (cat in categories) {
            val obj = JSONObject()
            obj.put("id", cat.id)
            obj.put("name", cat.name)
            val appsArray = JSONArray()
            for (pkg in cat.apps) {
                appsArray.put(pkg)
            }
            obj.put("apps", appsArray)
            obj.put("size", cat.size)
            obj.put("iconStyle", cat.iconStyle)
            obj.put("belowWatch", cat.belowWatch)
            obj.put("icon", cat.icon)
            array.put(obj)
        }
        val firstCatApps = categories.firstOrNull()?.apps ?: emptyList()
        prefs.edit()
            .putString(PREF_CATEGORIES, array.toString())
            .putString("order", firstCatApps.joinToString(","))
            .apply()
    }

    fun showTitles(prefs: SharedPreferences): Boolean = prefs.getBoolean(PREF_SHOW_TITLES, false)

    fun setShowTitles(prefs: SharedPreferences, show: Boolean) {
        prefs.edit().putBoolean(PREF_SHOW_TITLES, show).apply()
    }
}
