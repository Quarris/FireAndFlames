# 0.1.6
### Fixes
- Fixed the server hanging when a Crucible Controller was placed with open air all the way down
- Fixed alloying quietly stopping once the crucible was close to full
- Fixed entity melting rolling its chance more than once per entity
- Fixed items inserted by hoppers and pipes not always being saved
- Fixed ghost items appearing when a crucible was rebuilt smaller
- Recipes pointing at an empty fluid or item tag now name the tag instead of crashing

### Performance
- Crucibles no longer send their entire contents to nearby players every tick
