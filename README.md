# CustomizableBonemeal

Cusomizable bone meal. Shift-right-click to open a menu where you can select the plants you want to grow. Use bone meal as usual on grass to have the selected plants grow in the area.

## Compatibility

This plugin was made for Paper/Spigot 1.12.2. It is not expected to work properly with versions 1.13+.

## Permissions

`cbm.cbm` (**C**ustomizable **B**one **M**eal) is required to take advantage of the custom features.

## Configuration

Plants can be configured in the `config.yml`. The default configuration contains all flowers, tall flowers and tall grass in the game. The max amount of selected flowers can be changed.

```yaml
MaxSelections: 8
Plants:
  Sunflower:
    Material: double_plant
    Data: 0
  Lilac:
    Material: double_plant
    Data: 1
  DoubleTallgrass:
    Material: double_plant
    Data: 2
  LargeFern:
    Material: double_plant
    Data: 3
  RoseBush:
    Material: double_plant
    Data: 4
  Peony:
    Material: double_plant
    Data: 5
  Grass:
    Material: long_grass
    Data: 1
  Fern:
    Material: long_grass
    Data: 2
  Dandelion:
    Material: yellow_flower
    Data: 0
  Poppy:
    Material: red_rose
    Data: 0
  BlueOrchid:
    Material: red_rose
    Data: 1
  Allium:
    Material: red_rose
    Data: 2
  AzureBluet:
    Material: red_rose
    Data: 3
  RedTulip:
    Material: red_rose
    Data: 4
  OrangeTulip:
    Material: red_rose
    Data: 5
  WhiteTulip:
    Material: red_rose
    Data: 6
  PinkTulip:
    Material: red_rose
    Data: 7
  OxeyeDaisy:
    Material: red_rose
    Data: 8
```

## Events

`EntityChangeBlockEvent` is called on every changed block and the cancellation state is respected. Nothing happens if the event gets cancelled.
