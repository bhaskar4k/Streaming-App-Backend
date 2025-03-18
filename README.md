<h1 style="text-align: center; margin: 0; background-color: #e6e6e6; border-radius: 10px; padding: 20px;">Streaming-App</h1>


<h4 style="border-bottom: 2px solid black;">Versions</h4>
<b>Java :</b> v23.0.1 <br>
<b>Python :</b> v3.13.1 <br>
<b>Node :</b> v22.12.0 <br>
<b>NPM :</b> v10.9.0 <br>


<h4 style="border-bottom: 2px solid black;">Docker Setup</h4>
Download Docker Desktop from <a href="https://www.docker.com/products/docker-desktop/">here</a>.

<ul>
    <li>Download and install Docker Desktop.</li>
    <li>Open it.</li>
    <li>Open Powershell.</li>
    <li>Run this command : <code><b>docker run -d --hostname rmq --name rabbit-server -p 15672:15672 -p 5672:5672 rabbitmq:3-management</b></code></li>
</ul>

<h4 style="border-bottom: 2px solid black;">FFmpeg Setup</h4>
Download FFmpeg from <a href="https://drive.google.com/file/d/1iUe5nacH7ZJNpK8MJrom2VPskTnurFOi/view?usp=sharing">here</a>.

<ul>
    <li>Download and unzip.</li>
    <li>Update the ffmpeg and ffprobe path in all Environment.java file accross all microservices.</li>
    <ul>
        <li>C:/ffmpeg/bin/ffprobe.exe.</li>
        <li>C:/ffmpeg/bin/ffmpeg.exe.</li>
    </ul>
</ul>

<h4 style="border-bottom: 2px solid black;">Uploaded filepath</h4>
<ul>
    <li>Update the originalVideoPath, encodedVideoPath and pythonScriptPath in Environment.java file of Processing microservice. (pythonScript present in the python folder under Processing microservice.)</li>
    <li>Update the originalVideoPath, originalThumbnailPath in Environment.java file of Upload microservice.</li>
</ul>

<h4>Import the DB Dump from the repo</h4>