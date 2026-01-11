yt_dl_tool=yt-dlp
path_dl=../assets/download
tmp=./tmp
playlist=$path_dl/playlist
buf=buffer.tmp
last=last.tmp
ls1=ls1.tmp
ls2=ls2.tmp
list=playlits.tmp
stack=stack.tmp
common_args=" --no-warnings -q --progress"
cookies=../cookies.token


echo "Downloading ..."
echo $1
ls $path_dl > $tmp/$ls1
#Récupération du nom de la vidéo grâce à yt-dlp -e.
name=$($yt_dl_tool -t mp4 $1 $common_args --cookies $cookies)
echo $name
