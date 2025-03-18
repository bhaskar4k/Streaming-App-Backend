<h1 style="text-align: center; margin: 0; background-color: #e6e6e6; border-radius: 10px; padding: 20px;">Streaming-App</h1>


<h3 style="border-bottom: 2px solid black;">Versions</h3>
<b>Java :</b> v23.0.1 <br>
<b>Python :</b> v3.13.1 <br>
<b>Node :</b> v22.12.0 <br>
<b>NPM :</b> v10.9.0 <br>

<h3 style="border-bottom: 2px solid black;">Get Started</h3>
<ul>
    <li>Clone the repo.</li>
    <li><code><b>$ cd frontend-app</b></code></li>
    <li><code><b>$ npm i</b></code></li>
    <li><code><b>$ npm run dev</b></code></li>
    <li>Import the project's root folder in any JAVA's IDE.</li>
    <li>Sync all maven projects/Update maven.</li>
    <li>Change tomcat ports (if conflicts in your PC) from application.properties file of all microservices.</li>
</ul>

<h3 style="border-bottom: 2px solid black;">Docker Setup</h3>
Download Docker Desktop from <a href="https://www.docker.com/products/docker-desktop/">here</a>.

<ul>
    <li>Download and install Docker Desktop.</li>
    <li>Open it.</li>
    <li>Open Powershell.</li>
    <li>Run this command : <code><b>docker run -d --hostname rmq --name rabbit-server -p 15672:15672 -p 5672:5672 rabbitmq:3-management</b></code></li>
    <li>Start the docker container.</li>
</ul>

<h3 style="border-bottom: 2px solid black;">FFmpeg Setup</h3>
Download FFmpeg from <a href="https://drive.google.com/file/d/1iUe5nacH7ZJNpK8MJrom2VPskTnurFOi/view?usp=sharing">here</a>.

<ul>
    <li>Download and unzip it into root folder of C drive. (You can change if you want)</li>
    <li>Update the ffmpeg and ffprobe path in Environment.java file of Upload and Processing microservices. (Update the paths accordingly. <code>your_root_path_where_you_unzipped + "/ffmpeg/bin/ffprobe.exe"</code>)</li>
    <ul>
        <li>C:/ffmpeg/bin/ffprobe.exe.</li>
        <li>C:/ffmpeg/bin/ffmpeg.exe.</li>
    </ul>
</ul>

<h3 style="border-bottom: 2px solid black;">Update Env Filepath</h3>
<ul>
    <li>Update the originalVideoPath, encodedVideoPath and pythonScriptPath in Environment.java file of Processing microservice. (pythonScript present in the python folder under Processing microservice.)</li>
    <li>Update the originalVideoPath, originalThumbnailPath in Environment.java file of Upload microservice.</li>
</ul>

<h3>Import the DB Dump from the repo</h3>

And you're done..... :)