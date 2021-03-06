# [xitrum-placeholder](https://github.com/xitrum-framework/xitrum-placeholder)

[Xitrum](http://xitrum-framework.github.io/) implementation for [placehold.it](http://placehold.it/) running on [Heroku](http://xitrum-placeholder.herokuapp.com/)

## Usage

Use as placeholder inside img tag like below:

```html
<img src='http://xitrum-placeholder.herokuapp.com/150/100?color=black&text=xitrum&textcolor=00FF00'>
```

![sample](http://xitrum-placeholder.herokuapp.com/150/100?color=black&text=xitrum&textcolor=00FF00)

## API

```txt
/:size
```

return the square image of the specified size.

e.g. : [http://xitrum-placeholder.herokuapp.com/100](http://xitrum-placeholder.herokuapp.com/100)

![square](http://xitrum-placeholder.herokuapp.com/100)

```txt
/:width/:height
```

return the rectangle image of the specified width * height.

e.g. : [http://xitrum-placeholder.herokuapp.com/200/100](http://xitrum-placeholder.herokuapp.com/200/100)

![rectangle](http://xitrum-placeholder.herokuapp.com/200/100)

```txt
/circle/:radius
```

return the circle image of the specified radius.

e.g. : [http://xitrum-placeholder.herokuapp.com/circle/100](http://xitrum-placeholder.herokuapp.com/circle/100)

![circle](http://xitrum-placeholder.herokuapp.com/circle/100)

## Option query

customize image with query parameter.

e.g. : [http://xitrum-placeholder.herokuapp.com/100/100?color=red&text=hello&textcolor=white](http://xitrum-placeholder.herokuapp.com/100/100?color=red&text=hello&textcolor=white)

![circle](http://xitrum-placeholder.herokuapp.com/100/100?color=red&text=hello&textcolor=white)

### Available options

* color (rrggbb or ColorName) : see also => `<a href="http://www.docjar.com/docs/api/java/awt/Color.html" trget="_blank">Color</a>`
* text (String)
* textcolor (rrggbb or ColorName)

## Install local

```sh
git clone https://github.com/xitrum-framework/xitrum-placeholder.git
cd xitrum-placeholder.git
sbt/sbt fgRun
```

Running at http://localhost:8000/
