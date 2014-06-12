Chickenfoot Client for Android
------------------------------
Chickenfoot Client for Android

Setup mjpeg_streamer
--------------------

Now clone the mjpeg-streamer from the github repository in your folder:

    cd your/mjpg/streamer/folder
    git clone https://github.com/jacksonliam/mjpg-streamer.git .

In order to compile the code, we’ll need to install some library dependencies:

    sudo apt-get install libv4l-dev libjpeg8-dev imagemagick build-essential cmake subversion

Next, we’ll need to compile the mjpeg-streamer:

    cd mjpg-streamer-experimental
    make

Now we should be set to start streaming the video. If you lower the resolution, the latency will get smaller:

    export LD_LIBRARY_PATH=.
    ./mjpg_streamer -o "output_http.so -w ./www -p 8090" -i "input_raspicam.so -x 640 -y 480 -fps 20 -ex night"

License
-------
MIT © Andrea Stagi