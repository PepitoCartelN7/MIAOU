src_dir=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

python="$src_dir/../bin/python3"
yt_dl_tool="$src_dir/../bin/yt-dlp"
path_dl="$src_dir/../assets/download"
tmp="$src_dir/tmp"
playlist=$path_dl/playlist
buf=buffer.tmp
last=last.tmp
ls1=ls1.tmp
ls2=ls2.tmp
list=playlits.tmp
stack=stack.tmp
cookies=$src_dir/../cookies.token
common_args="--no-warnings -q --no-progress"

function playlist() {
	if [[ -z $2 ]]; then args=$common_args; else args="$common_args $2 --sleep-interval 5"; fi
	echo "playlist"
	echo "cleaning previous playlist folder, following file will be removed"
	echo $(ls $playlist)
	rm $tmp/$last ; touch $tmp/$last
	rm $playlist/*.mp3
	$yt_dl_tool -e $1 $args > $tmp/$list
	if [[ -z $(cat $tmp/$list) ]] then
		echo "Error trying to find the playlist!"
		return
	fi
	index=1
	cat $tmp/$list | while read LINE; do
		#cleaning the name of the video to remove special character and replace space by _ .
		#prout jp
		name=${LINE//[^[:alnum:] ]/}
		name=${name// /_}.mp3
		name=${name//__/_}
		while true; do
			ls $playlist > $tmp/$ls1
			echo "Downloading music n°$index : $name"
			$yt_dl_tool -t mp3 $1 -o $playlist/$name -I $index $args > $tmp/$stack; ls $playlist > $tmp/$ls2
			((index++))
			if [[ $(diff $tmp/$ls1 $tmp/$ls2) == *$name* ]] then
				echo "Vid is downloaded here : $playlist/$name"
				break
			else
				echo ${name:0:${#name}-5}
			fi
		done
		echo $name >> $tmp/$last
		rm $tmp/$ls1; rm $tmp/$ls2
		rm $tmp/$stack
	done
	echo "Whole playlist is downloaded"
	echo "Exiting"
	rm $tmp/$list
}

function init () {
	echo "Initialisation ..."
	mkdir $path_dl
	rm $path_dl/*.mp3
	mkdir $tmp
	mkdir $playlist
	rm $playlist/*.mp3
	rm $tmp/$buf
	touch $tmp/$buf
	ver=$($python $yt_dl_tool --version)
	if [[ -z $ver ]] then
		echo "Error, yt-dlp is either not installed, or not correctly renseigned."
		echo "If yt-dlp is not installed, please go there and follow the instructions https://github.com/yt-dlp/yt-dlp?tab=readme-ov-file#installation"
		echo "If yt-dlp is installed, try editing this file to put the proper path to ytdlp in the yt_dl_tool variable."
		echo "If it is still not working : try crying very loudly, dipping you computer in the blood of a baby goat or, in absolutely last resort, contact me."
	fi
	cok=$(cat $cookies)
	if [[ -z $cok ]] then
		echo "Error, cookies file not founds."
		echo "You won't be able to use the -m or --mature option."
		echo "If you don't know how to setup cookies files, more informations here: https://github.com/yt-dlp/yt-dlp/wiki/Extractors#exporting-youtube-cookies"
		echo "Please keep in mind that using your account cookies to download youtube videos is a great way to get restricted it or banned."
	fi
}

function download () {
	if [[ -z $2 ]]; then args=$common_args; else args="$common_args $2"; fi
	echo "Downloading : $1"
	ls $path_dl > $tmp/$ls1
	#Récupération du nom de la vidéo grâce à yt-dlp -e.
	name=$($python $yt_dl_tool -e $1 $args --no-playlist ); echo $name
	#-z $name vaut true si $name est vide i.e. il y a un problème.
	if [[ -z $name ]] then
		echo "Error trying to find the video!"
		rm $tmp/$ls1
		return
	fi
	#cleaning the name of the video to remove special character and replace space by _ .
	name=${name//[^[:alnum:] ]/}
	name=${name// /_}.mp3
	name=${name//__/_}
	#Finally, the proper downloading of the video music.
	$python $yt_dl_tool -t mp3 $1 -o $path_dl/$name $args --no-playlist > $tmp/$stack ; echo "Download finished."
	#Verifying that everything worked as intended
	ls $path_dl > $tmp/$ls2
	if [[ $(diff $tmp/$ls1 $tmp/$ls2) == *$name* ]]; then
		echo "Vid is downloaded here : $path_dl/$name"
		echo $name >> $tmp/$buf
		echo $name > $tmp/$last
		rm $tmp/$ls1; rm $tmp/$ls2
	else
		echo "Something broke! Full stack below or in $stack ."
		rm $tmp/$ls1; rm $tmp/$ls2
		cat $tmp/$stack
	fi
}

function clean() {
	echo "Cleaning ..."
	rm $path_dl/*.mp3
	rm $tmp/*.tmp
	touch $tmp/$buf
	rm $playlist/*.mp3
}

function update() {
	echo "Updating ..."
	$python $yt_dl_tool -U
}

function license() {
	echo "This is open-source I guess, I am too lazy to do otherwise."
	echo "yt-dlp is free and in the public domain, more info here : https://github.com/yt-dlp/yt-dlp?tab=Unlicense-1-ov-file#readme"
	echo "Please note that using third party tools (like this script to download content) is against youtube terms of service."
	echo "Not that I care obviously, but it's important to note, and more importantly, it's a lot funnier when it's illegal."
	echo "This was made with the help of a lot of random post on StackOverflow from people I'm too lazy to credit."
}

function remove-oldest() {
	{ read old ; cat > $tmp/tampon.tmp; } < $tmp/$buf
	if [[ -z $old ]] then
		echo "Nothing to remove"
	else
		rm -v $path_dl/$old
		cat $tmp/tampon.tmp > $tmp/$buf
	fi
	rm $tmp/tampon.tmp
}

function help() {
	echo "Usage: $(basename $0) [-d URL [-m]] [-h] [-i] [-c] [-ro] [-u] [-p URL [-m]]"
	echo "Options:"
	echo " -i, --init                 prepare mandatory directories for the script to work."
	echo " -u, --update               update yt-dlp, basicaly just run \"$yt_dl_tool -u\" ."
	echo " -h, --help                 Display this help message."
	echo " -d, --download [URL] [-m]  Download the video from link [URL] in $path_dl folder, overwrite $tmp/$last with the file name."
	echo "                            add -m or --mature to use cookies to be able to download age restricted videos [USE RESPONSIBLY]"
	echo " -c, --clean                empty $path_dl folder, and reset $tmp folder."
	echo " -ro, --remove-oldest       remove the oldest downloaded element, to keep things tidy."
	echo " -p, --playlist [URL] [-m]  download a playlist from URL in $path_dl folder, overwrite $tmp/$last with the list of song names."
	echo "                            add -m or --mature to use cookies to be able to download age restricted videos [USE RESPONSIBLY] "
	echo "                            Please note that this is also quite slower to (5sec of waiting between download request) to reduce risk"
	echo " -l, --license              diplay license information and some terms of service precisions (just use this script without reading like everybody)."
}

case "$1" in
	"-i" | "--init")
		init
		;;
	"-u" | "--update")
		update
		;;
	"-h" | "--help")
		help ;;
	"-d" | "--download")
		if [[ "$3" == "-m" || "$3" == "--mature" ]]; then cok="--cookies $cookies"; else cok=""; fi
		download $2 "$cok";;
	"-c" | "--clean")
		clean ;;
	"-ro" | "--remove-oldest")
		remove-oldest ;;
	"-p" | "--playlist")
		if [[ "$3" == "-m" || "$3" == "--mature" ]]; then cok="--cookies $cookies"; else cok=""; fi
		playlist $2 "$cok";;
	"-l" | "--license")
		license ;;
	"")
		echo "Error: You must provide at least one argument."
		echo "Type $(basename $0) --help to see a list of all options.";;
	*)
		echo "Unknown argument: $1" ;;
esac
