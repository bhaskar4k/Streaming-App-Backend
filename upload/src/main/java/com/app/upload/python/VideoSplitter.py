import os
import sys
from moviepy import VideoFileClip

def split_video_into_chunks(filepath, folderpath, chunk_duration):
    os.makedirs(folderpath, exist_ok=True)
    try:
        video = VideoFileClip(filepath)
        video_duration = video.duration
        file_basename = os.path.splitext(os.path.basename(filepath))[0]

        start_time = 0
        chunk_index = 1

        while start_time < video_duration:
            end_time = min(start_time + chunk_duration, video_duration)
            chunk_filename = os.path.join(folderpath, f"{file_basename}_chunk{chunk_index}.mp4")
            video.subclipped(start_time, end_time).write_videofile(chunk_filename, codec="libx264", audio_codec="aac")
            start_time += chunk_duration
            chunk_index += 1

        video.close()
        print("Video splitting completed.")

    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    # Command-line arguments: filepath, folderpath, chunk_duration
    filepath = sys.argv[1]
    folderpath = sys.argv[2]
    chunk_duration = 5
    # filepath = "E:\\My Videos\\Kinemaster\\Amkash.mp4"
    # folderpath = "E:\\My Videos\\Kinemaster\\JOD"
    # chunk_duration = 5
    split_video_into_chunks(filepath, folderpath, chunk_duration)
