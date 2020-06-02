import sched, time
from pydub.playback import play
from pydub import AudioSegment
from gtts import gTTS



s = sched.scheduler(time.time, time.sleep)
array = ["vamos para a direita", "vamos para a esquerda","vamos para cima","vamos para baixo","bati num obstáculo","cheguei ao fim"]





def playAudio(array):

    for i in array:
        audio = AudioSegment.from_file(i+".mp3")
        play(audio)
        time.sleep(5)

    #sc.enter(1, 1, do_something, (sc,))
    #sc.run()


text = "bateu num obstáculo"
language = 'pt'
speech = gTTS(text=text, lang=language, slow=False)
speech.save("bateu num obstaculo.mp3")

#
# translator = Translator()
# result = translator.translate("um", src='pt', dest='en')
# num = str(result.text)
# a = w2n.word_to_num(num)
# print(a)
