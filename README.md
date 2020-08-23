# jfxrunner

Launcher for programs that don't bundle JavaFX

## Compatibility

Supports Java 8+ (tested with Java 8, Java 11, Java 14)

If JavaFX is not found then [OpenJFX](https://openjfx.io/) 11.0.2 will be downloaded

## Example usage

Execute `my_program.jar` with command line arguments

```
java -jar jfxrunner.jar  my_program.jar  args to pass to my_program
```

<br>

Execute `my_program.jar` with custom path to JavaFX

```
java -jar jfxrunner.jar  -jfx /path/to/jfx/lib  my_program.jar
```

## Parameters

Parameters must be come before `my_program.jar`

```
-javaw                    run in background using javaw (default: false, use java and inherit I/O)
-jfx     /path/           specifies the path to JavaFX  (default: env.PATH_TO_FX, download)
-modules javafx.controls  specifies the modules to load (default: all JavaFX modules)
-help                     display usage info
```
