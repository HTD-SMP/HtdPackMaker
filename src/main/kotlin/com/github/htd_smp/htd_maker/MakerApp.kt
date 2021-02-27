package com.github.htd_smp.htd_maker

import com.github.htd_smp.htd_pack_reader.HtdPackV1
import com.github.htd_smp.htd_pack_reader.PackReader
import com.therandomlabs.curseapi.CurseAPI
import com.therandomlabs.curseapi.minecraft.MCVersionGroups
import com.therandomlabs.curseapi.minecraft.MCVersions
import com.therandomlabs.curseapi.project.CurseProject
import com.therandomlabs.curseapi.project.CurseSearchQuery
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.scene.control.SelectionMode
import tornadofx.*

object Global {
    val addedMods = mutableListOf<String>().asObservable()
    val addedModsProjects = mutableListOf<CurseProject>()

    val rvers = MCVersions.getAll().filter { it.toString().matches(".*\\d.*".toRegex()) } // dont include versions without a number, like "Forge", "Fabric", etc...
    val selectedVers = SimpleStringProperty(rvers.last().toString())
}

class MakerApp : App(MakerView::class) {
}

class MakerView : View() {
    private val mc = CurseAPI.game(432).get()

    override val root = form {
        fieldset {
            button("Add mods") {
                action {
                    AddModsView().openWindow()
                }
            }
            val versions = mutableListOf<String>()
            for(version in Global.rvers) {
                versions.add(version.toString())
            }
            combobox(Global.selectedVers, FXCollections.observableList(versions)) {
                Global.selectedVers.addListener(tornadofx.ChangeListener { observable, oldValue, newValue ->
                    val modsToRem = mutableListOf<CurseProject>()
                    for(project in Global.addedModsProjects) {
                        if(!project.files().any{ file -> file.gameVersionStrings().any{ it == Global.selectedVers.get() } }) {
                            modsToRem.add(project)
                        }
                    }
                    for(mod in modsToRem) {
                        Global.addedMods.remove(mod.name())
                        Global.addedModsProjects.removeIf { it.id() == mod.id() }
                    }
                })
//                { l: ChangeListener<in String> ->
//
//                }
            }
            label("Mod list:")
            listview(Global.addedMods) {
                selectionModel.selectionMode = SelectionMode.SINGLE
                onUserSelect { selected ->
                    ModView(
//                        CurseAPI.searchProjects(CurseSearchQuery().game(CurseAPI.game(432).get())).get()[0]
                        Global.addedModsProjects.find { it.name() == selected }!!
                    ).openWindow();
                }
            }
            button("Export") {
                action {
                    val mods = mutableListOf<HtdPackV1.Mod>()
                    for(mod in Global.addedModsProjects) {
                        val latestVer = mod.files().toList().filter{ it.gameVersionStrings().contains(Global.selectedVers.get()) }.last()
                        mods.add(HtdPackV1.CurseMod(mod.id().toString(), mod.slug(), latestVer.nameOnDisk(),latestVer.id().toString()))
                    }
//                    println(HtdPackV1(mods, HtdPackV1.ModLoader.FORGE, Global.selectedVers.get()).toJson())
                    ExportView(HtdPackV1(mods, HtdPackV1.ModLoader.FORGE, Global.selectedVers.get())).openWindow()
                }
            }
        }
    }
}

class AddModsView : View() {
    private val mc = CurseAPI.game(432).get()
    var projects = CurseAPI.searchProjects(CurseSearchQuery().game(mc).categorySectionID(6).gameVersionString(Global.selectedVers.get())).get().asObservable();

    var projectNames = mutableListOf<String>().asObservable();

    override val root = form {
        for(project in projects) {
            projectNames.add(project.name());
        }
        label("Add mods")
        textfield {
            action {
//                println(mc.categorySections())
                val searchQuery = CurseSearchQuery().game(mc).categorySectionID(6).gameVersionString(Global.selectedVers.get());
                if(text.isNotEmpty()) {
                    searchQuery.searchFilter(text)
                }
                projects = CurseAPI.searchProjects(searchQuery).get().asObservable()
                projectNames.clear()
                for(project in projects) {
                    projectNames.add(project.name());
                }
            }
        }
        listview(projectNames) {
            selectionModel.selectionMode = SelectionMode.SINGLE
            onUserSelect { str ->
                ModView(projects.find { it.name() == str }!!).openWindow()
            }
        }
//        for(game in CurseAPI.games().get()) {
//            println("${game.id()} : ${game.name()}")
//        }
//        listview(mc.)
    }
}