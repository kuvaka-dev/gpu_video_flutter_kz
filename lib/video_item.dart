class VideoItem {
  String path;
  int duration;
  int width;
  int height;

  VideoItem(
      {required this.path,
      required this.duration,
      required this.width,
      required this.height});

  factory VideoItem.fromJson(Map<String, dynamic> json) {
    return VideoItem(
        path: json["path"],
        duration: json["duration"],
        width: json["width"],
        height: json["height"]);
  }
}
