# results - web-based timing and scoring software for ski- and running races

## The frontend

The frontend is based on [React](https://reactjs.org/), and the state management is following [MEIOSIS](https://meiosis.js.org) -pattern by utilizing [flyd](https://github.com/paldepind/flyd) and [Patchinko](https://github.com/barneycarroll/patchinko). I did some investigation of commonly used state management libraries such as Redux and Mobx, but it quickly seemed far too tedious to get grip of them, as I'm still a bit newbie with js. Besides, doing it on my own way sound much more fun! 

Of course the app has lot more dependencies, but they're all included in the [package.json](webui/webpack.json) -file. The building is based on webpack, by typing:

```
cd webui
npx webpack --mode production
```

## The backend

## The data

## Running it

TODO


