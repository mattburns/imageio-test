# ImageIO test

I've found some differences with the decoded RGB pixel values output from Java's ImageIO, when compared with Python and ImageMagick. This project is just a simple test case the proves the problem.

## Dependencies

Uses the Python Imaging Library (PIL)

    pip install Pillow

And also requires ImageMagick (specifically, the `convert` command)

## Run it!

    ./gradlew test
    
## Here's the output:

    Image IO    : [0, 0] = [145, 146, 164]
    Python      : [0, 0] = [145, 146, 164]
    ImageMagick : [0, 0] = [145, 146, 164]

    Image IO    : [1, 0] = [137, 138, 156] <--- different to python
    Python      : [1, 0] = [137, 139, 154]
    ImageMagick : [1, 0] = [137, 139, 154]

    Image IO    : [2, 0] = [148, 147, 161]
    Python      : [2, 0] = [148, 147, 161]
    ImageMagick : [2, 0] = [148, 147, 161]

    Image IO    : [0, 1] = [150, 153, 168]
    Python      : [0, 1] = [150, 153, 168]
    ImageMagick : [0, 1] = [150, 153, 168]

    Image IO    : [1, 1] = [138, 141, 156] <--- different to python
    Python      : [1, 1] = [138, 142, 154]
    ImageMagick : [1, 1] = [138, 142, 154]

    Image IO    : [2, 1] = [145, 147, 159]
    Python      : [2, 1] = [145, 147, 159]
    ImageMagick : [2, 1] = [145, 147, 159]

    Image IO    : [0, 2] = [154, 160, 172]
    Python      : [0, 2] = [154, 160, 172]
    ImageMagick : [0, 2] = [154, 160, 172]

    Image IO    : [1, 2] = [146, 152, 164] <--- different to python
    Python      : [1, 2] = [146, 153, 163]
    ImageMagick : [1, 2] = [146, 153, 163]

    Image IO    : [2, 2] = [144, 148, 157] <--- different to python
    Python      : [2, 2] = [144, 148, 159]
    ImageMagick : [2, 2] = [144, 148, 159]
