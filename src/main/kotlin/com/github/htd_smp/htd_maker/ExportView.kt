package com.github.htd_smp.htd_maker

import com.github.htd_smp.htd_pack_reader.HtdPackV1
import com.github.htd_smp.htd_pack_reader.PackReader
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import tornadofx.*
import java.io.File
import javax.swing.JFileChooser
import java.io.FileWriter





class ExportView(pack: HtdPackV1) : View() {
    override val root = form {
        val json = "HTDPack JSON"
        val cf = "CurseForge pack"
        val server = "Server pack"
        val type = SimpleStringProperty(json)
        label("Export as:")
        combobox(type, FXCollections.observableArrayList(json, cf, server))
        button("Export!") {
            action {
                // TODO: Move this to its own project

                val f = JFileChooser()
                f.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                f.showSaveDialog(null)

                val file = f.selectedFile
                when (type.get()) {
                    json -> {
                        val myWriter = FileWriter(file.absolutePath + "/HTD-PACK.json")
                        myWriter.write(pack.toJson())
                        myWriter.close()
                    }
                    cf -> {

                    }
                    server -> {

                    }
                }

            }
        }
    }
}