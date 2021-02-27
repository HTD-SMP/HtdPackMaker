package com.github.htd_smp.htd_maker

import com.therandomlabs.curseapi.project.CurseProject
import javafx.scene.control.OverrunStyle
import javafx.scene.text.Font
import tornadofx.*
import java.awt.Desktop
import java.net.URI
import kotlin.math.min

class ModView(project: CurseProject) : View() {
    override val root = form {
        val added = Global.addedModsProjects.any { it.id() == project.id() }
        togglebutton(if (!added) "Add" else "Remove") {
            isSelected = !added

            action {
                isSelected = !Global.addedModsProjects.any { it.id() == project.id() }
                text = if (!isSelected) "Add" else "Remove"
                if(isSelected) {
                    Global.addedMods.add(project.name())
                    Global.addedModsProjects.add(project)
                } else {
                    Global.addedMods.remove(project.name())
                    Global.addedModsProjects.removeIf { it.id() == project.id() }
                }
            }
        }
        hyperlink(project.name()) {
            font = Font.font(40.0)
            action {
                Desktop.getDesktop().browse(URI(project.url().toString()))
            }
        }
        hyperlink("By: ${project.author().name()}") {
            action {
                Desktop.getDesktop().browse(URI(project.author().url().toString()))
            }
        }
        label(project.summary())
//        var desc = project.description().wholeText()
//        desc = desc.substring(0, min(desc.length, 500));
//        label(desc) {
//            maxWidth = 800.0
//            textOverrun = OverrunStyle.WORD_ELLIPSIS
//        }
    }
}