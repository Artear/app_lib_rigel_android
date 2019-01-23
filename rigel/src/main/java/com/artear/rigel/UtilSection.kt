package com.artear.rigel


fun getUniqueId(section: NavigationSection, id: String): String {
    return "${section.titleSection.first()}_${section.position}_$id"
}
