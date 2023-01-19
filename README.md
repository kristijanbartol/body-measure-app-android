# BodyMeasure Android app

This is a rudimentary Android application for body measurement from images. It takes a photo of a person, extracts silhouette, and estimates 15 
body measurements. The application uses two algorithms of choice for silhouette extraction - background subtraction or DeepLabv3 model (the instructions
on how to set up these options are specified below).

## Development setup

### Step 1

Enable camera by updating your build.gradle of the module BodyApp.app:

```
dependencies {
    ...
    def camerax_version = "1.0.0-alpha05"
    implementation "androidx.camera:camera-core:$camerax_version"
    implementation "androidx.camera:camera-camera2:$camerax_version"
    ...
}
```

and adding camera permission by adding these lines after `super.onCreate(savedInstanceState);` line in onCreate method:

```
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
    }
    ...
```

### Step 2

Use pytorch_android_lite:1.9.0 and pytorch_android_torchvision:1.9.0 or 1.10.0 by specifying them in the build.gradle:

```
dependencies {
    ...
    implementation 'org.pytorch:pytorch_android_lite:1.10.0'
    implementation 'org.pytorch:pytorch_android_torchvision_lite:1.10.0'
    ...
}
```

### Step 3 

In order to use background subtraction part, add OpenCV as a dependency:

  - [ ] Download latest OpenCV sdk for Android from OpenCV.org and decompress the zip file.
  
  - [ ] Open the sdk folder as an Android Studio project and comment out the line 
  `apply plugin: ‘kotlin-android’` in build.gradle file of opencv plugin.

  - [ ] Import OpenCV to Android Studio, From File -> New -> Import Module, choose sdk/java folder in the unzipped 
opencv archive.

  - [ ] Update build.gradle under imported OpenCV module to update 4 fields to match your project build.gradle 
a) compileSdkVersion b) buildToolsVersion c) minSdkVersion and d) targetSdkVersion.

  - [ ] Add module dependency by Application -> Module Settings, and select the Dependencies tab. Click + icon at bottom, 
choose Module Dependency and select the imported OpenCV module (sdk/ folder). You can keep the name "sdk". For newer version of 
Android Studio, to access to Module Settings : in the project view, right-click the  dependent module -> Open Module Settings.

  - [ ] In case you get the error `More than one file was found with OS independent path 'lib/armeabi-v7a/libc++_shared.so'`,
  use [this solution](https://stackoverflow.com/questions/44954122/more-than-one-file-was-found-with-os-independent-path-lib-x86-libusb-so).


### Step 4

In order to use DeepLabv3 part, first create `deeplabv3_scripted_optimized.ptl` scripted model in Python:

```
import torch
from PIL import Image
from torchvision import transforms
from torch.utils.mobile_optimizer import optimize_for_mobile

# use deeplabv3_resnet50 instead of resnet101 to reduce the model size
model = torch.hub.load('pytorch/vision:v0.7.0', 'deeplabv3_resnet50', pretrained=True)
model.eval()

scriptedm = torch.jit.script(model)
optimized_traced_model = optimize_for_mobile(scriptedm)
optimized_traced_model._save_for_lite_interpreter("deeplabv3_scripted_optimized.ptl")


input_image = Image.open("deeplab.jpg")
preprocess = transforms.Compose([
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225]),
])

input_tensor = preprocess(input_image)
input_batch = input_tensor.unsqueeze(0)
with torch.no_grad():
    output = model(input_batch)['out'][0]
```

or use `deeplabv3_scripted_optimized.py` script in the [corresponding Python repository](https://github.com/kristijanbartol/body-measure-from-images).
