# cosmos-raytracer

<p align="center">
    <img src="https://github.com/PRTTMPRPHT/cosmos-raytracer/raw/master/SAMPLE_IMAGE.png" alt="Sample Image" width="320" height="240">
</p>

This is a small raytracer written in Clojure for the Functional Programming course of my university, way back in 2019.
The assignment was to create a program that utilises all of the Clojure features presented in the lecture.

This raytracer produces a single image composed of spheres, planes and point lights.

## Usage 

This project can be launched with any clojure runtime you have available.
Using a normal `clojure.jar`:

```
java -cp "clojure.jar;src" clojure.main src/cosmos/main.clj
```

You can reconfigure the entire scene rendered by editing `src/cosmos/scene_config.clj`.

## License

By contributing, you agree that your contributions will be licensed under its [Unlicense](http://unlicense.org/).