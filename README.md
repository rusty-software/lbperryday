# lbperryday

A video game about how to live LBPERRYDAY

## Overview

This might very well be the best vidya game ever.  Play the game by visiting [rusty-software.github.io/lbperryday/](http://rusty-software.github.io/lbperryday/)

## Development Details

This is a figwheel/reagent project.  Pointers about how to get it going locally are below (and came from the README template).

To get an interactive development environment run:

    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).
This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    lein clean

To create a production build run:

    lein do clean, cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL.

## Publishing

The game is published to github pages.  To publish a new version, check out the `gh-pages` branch, then:

    git pull origin master

This will effectively merge the head of the master branch into the gh-pages branch.  After this is completed, do the clean and build dance mentioned above.

    lein do clean, cljsbuild once min

This will produce the correct artifacts on the branch.  The last thing to do is copy the artifacts to the right spot and push the branch back to github.

    cp resources/public/js/compiled/lbperryday.js js/compiled/
    git commit -a "Publishing update"
    git push

## License

Copyright © 2016 rusty-software

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
