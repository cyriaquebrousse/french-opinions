# run_mallet.sh
# Use: sh run_mallet.sh language inputdir numtopics
#      (language must be in lowercase two-letter form)

# Parameters
language="$1"
inputdir="$2"
numtopics="$3"

malletfile="topics/.mallet"
outputkeys="topics/keys.txt"
outputcompo="topics/compo.txt"
inferencer="topics/inferencer.ser"


# Importing the corpus directory
./mallet/bin/mallet import-dir --input "$inputdir" --output "$malletfile" --keep-sequence --token-regex "[\p{L}\p{M}]+" --remove-stopwords --stoplist-file lib/mallet_stoplist_"$language".txt

# Training the topic model
./mallet/bin/mallet train-topics --input "$malletfile" --num-topics "$numtopics" --output-topic-keys "$outputkeys" --output-doc-topics "$outputcompo" --num-top-words 16 --optimize-interval 20