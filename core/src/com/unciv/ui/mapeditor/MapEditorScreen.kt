package com.unciv.ui.mapeditor

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.unciv.models.metadata.GameParameters
import com.unciv.logic.MapSaver
import com.unciv.logic.map.TileMap
import com.unciv.models.gamebasics.tr
import com.unciv.ui.tilegroups.TileGroup
import com.unciv.ui.tilegroups.TileSetStrings
import com.unciv.ui.utils.CameraStageBaseScreen
import com.unciv.ui.utils.onClick
import com.unciv.ui.utils.setFontSize
import com.unciv.ui.worldscreen.TileGroupMap

class MapEditorScreen(): CameraStageBaseScreen(){
    var tileMap = TileMap(GameParameters())
    var mapName = "My first map"
    lateinit var mapHolder: TileGroupMap<TileGroup>
    val tileEditorOptions = TileEditorOptionsTable(this)


    constructor(mapNameToLoad:String?):this(){
        var mapToLoad = mapNameToLoad
        if (mapToLoad == null) {
            val existingSaves = MapSaver().getMaps()
            if(existingSaves.isNotEmpty())
                mapToLoad = existingSaves.first()
        }
        if(mapToLoad!=null){
            mapName=mapToLoad
            tileMap= MapSaver().loadMap(mapName)
        }
        initialize()
    }

    constructor(map: TileMap):this(){
        tileMap = map
        initialize()
    }

    fun initialize() {
        tileMap.setTransients()
        val mapHolder = getMapHolder(tileMap)

        stage.addActor(mapHolder)

        stage.addActor(tileEditorOptions)


        val optionsMenuButton = TextButton("Options".tr(), skin)
        optionsMenuButton.onClick {
            MapEditorOptionsTable(this)
        }
        optionsMenuButton.label.setFontSize(24)
        optionsMenuButton.labelCell.pad(20f)
        optionsMenuButton.pack()
        optionsMenuButton.x = 30f
        optionsMenuButton.y = 30f

        stage.addActor(optionsMenuButton)
    }

    private fun getMapHolder(tileMap: TileMap): ScrollPane {
        val tileSetStrings = TileSetStrings()
        val tileGroups = tileMap.values.map { TileGroup(it, tileSetStrings) }
        for (tileGroup in tileGroups) {
            tileGroup.showEntireMap = true
            tileGroup.update()
            tileGroup.onClick {
                val tileInfo = tileGroup.tileInfo

                tileEditorOptions.updateTileWhenClicked(tileInfo)
                tileGroup.tileInfo.setTransients()
                tileGroup.update()
            }
        }

        mapHolder = TileGroupMap(tileGroups, 300f)
        val scrollPane = ScrollPane(mapHolder)
        scrollPane.setSize(stage.width, stage.height)
        scrollPane.layout()
        scrollPane.scrollPercentX=0.5f
        scrollPane.scrollPercentY=0.5f
        scrollPane.updateVisualScroll()
        return scrollPane
    }


}

