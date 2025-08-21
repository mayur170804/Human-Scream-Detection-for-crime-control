import numpy as np
import sounddevice as sd
import librosa
import queue
from tensorflow.keras.models import load_model
import os
import warnings
import geocoder
from geopy.geocoders import Nominatim
import time
import smtplib
from email.message import EmailMessage
import cv2

# Suppress TensorFlow and Python warnings
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'
warnings.filterwarnings("ignore")

# Load the trained model
MODEL_PATH = os.path.join("ensemble_predictor", "cnn_scream_model.h5")
model = load_model(MODEL_PATH)

# Audio settings
SAMPLE_RATE = 22050
DURATION = 1  # seconds
BLOCK_SIZE = int(SAMPLE_RATE * DURATION)

# Shared queue to pass audio blocks from callback to main thread
audio_queue = queue.Queue()

def calculate_rms_level(audio_data):

    rms = np.sqrt(np.mean(audio_data**2))
    level = int(min(100, (rms * 10000)))  # Scale RMS to 0–100 range
    print(f"LEVEL: {level}", flush=True)

def get_location_info():
    try:
        g = geocoder.ip('me')
        if g.ok:
            lat, lon = g.latlng
            geolocator = Nominatim(user_agent="scream-detector")
            location = geolocator.reverse((lat, lon), exactly_one=True)
            address = location.raw.get("address", {})
            road = address.get("road", "")
            neighbourhood = address.get("neighbourhood", "")
            city = address.get("city", address.get("town", address.get("village", "")))
            state = address.get("state", "")
            postcode = address.get("postcode", "")
            return f"{road}, {neighbourhood}, {city}, {state}, {postcode}".strip(', ')
    except Exception as e:
        print(f"⚠️ Location fetch error: {e}", flush=True)
    return "Location unavailable"

def predict_label_from_audio(audio_data, sample_rate=22050):
    mfcc = librosa.feature.mfcc(y=audio_data, sr=sample_rate, n_mfcc=40)

    if mfcc.shape[1] < 174:
        pad_width = 174 - mfcc.shape[1]
        mfcc = np.pad(mfcc, pad_width=((0, 0), (0, pad_width)), mode='constant')
    else:
        mfcc = mfcc[:, :174]

    mfcc = mfcc[..., np.newaxis]
    mfcc = np.expand_dims(mfcc, axis=0)

    probs = model.predict(mfcc, verbose=0)[0]
    confidence = float(probs)
    if confidence >= 0.95:
        label = "Scream"
    else:
        label = "Non-Scream"
    return label, confidence

def send_email_with_log_file(file_path):
    try:
        msg = EmailMessage()
        msg['Subject'] = '🚨 Scream Detected - Location Log'
        msg['From'] = "mayur.s17084@gmail.com"
        msg['To'] = "mayur.s17084@gmail.com"
        msg.set_content("A scream was detected. Attached is the location log.")

        with open(file_path, 'rb') as f:
            file_data = f.read()
            file_name = os.path.basename(file_path)

        msg.add_attachment(file_data, maintype='text', subtype='plain', filename=file_name)

        with smtplib.SMTP_SSL('smtp.gmail.com', 465) as smtp:
            smtp.login("mayur.s17084@gmail.com", "dedo jxdz obgr svng")
            smtp.send_message(msg)
        print("📧 Email sent successfully!", flush=True)
    except Exception as e:
        print(f"❌ Failed to send email: {e}", flush=True)


# Send email with video attachment after scream detection
def send_email_with_video(video_path):
    try:
        msg = EmailMessage()
        msg['Subject'] = '🚨 Scream Detected - Video Clip'
        msg['From'] = "mayur.s17084@gmail.com"
        msg['To'] = "mayur.s17084@gmail.com"
        msg.set_content("Attached is the video clip recorded after a scream detection.")

        with open(video_path, 'rb') as f:
            video_data = f.read()
            video_name = os.path.basename(video_path)

        msg.add_attachment(video_data, maintype='video', subtype='avi', filename=video_name)

        with smtplib.SMTP_SSL('smtp.gmail.com', 465) as smtp:
            smtp.login("mayur.s17084@gmail.com", "dedo jxdz obgr svng")
            smtp.send_message(msg)
        print("📧 Video email sent successfully!", flush=True)
    except Exception as e:
        print(f"❌ Failed to send video email: {e}", flush=True)

def audio_callback(indata, frames, time_info, status):
    if status:
        print("⚠️", status)
    audio_queue.put(indata[:, 0].copy())

def listen_and_predict():
    print("⏳ Initializing audio stream...")
    with sd.InputStream(callback=audio_callback, channels=1, samplerate=SAMPLE_RATE, blocksize=BLOCK_SIZE):
        print("🎙️ Listening for screams... (Press Ctrl+C to stop)")
        try:
            while True:
                if not audio_queue.empty():
                    audio_data = audio_queue.get()
                    rms = np.sqrt(np.mean(audio_data**2))
                    calculate_rms_level(audio_data)
                    if rms > 0.02:
                        label, conf = predict_label_from_audio(audio_data)
                        print(label, flush=True)

                        if label == "Scream":
                            location = get_location_info()
                            print(f"🚨 Scream detected at {time.strftime('%H:%M:%S')}!")
                            print(f"📍 Location: {location}", flush=True)

                            # Log the timestamp, location, and Google Maps URL to a text file
                            timestamp = time.strftime('%Y-%m-%d %H:%M:%S')
                            g = geocoder.ip('me')
                            lat, lon = g.latlng if g.ok else ("Unknown", "Unknown")
                            maps_url = f"https://www.google.com/maps?q={lat},{lon}"
                            with open("scream_location_log.txt", "a") as f:
                                f.write(f"{timestamp} - Location: {location} - Map: {maps_url}\n")

                            send_email_with_log_file("scream_location_log.txt")
                    else:
                        print("🔇 Too quiet - skipping", flush=True)
        except KeyboardInterrupt:
            print("\n🛑 Stopped listening.")

if __name__ == "__main__":
    listen_and_predict()