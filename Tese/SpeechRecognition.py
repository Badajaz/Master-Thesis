import speech_recognition as sr
from word2number import w2n
from googletrans import Translator
from pydub.playback import play
from pydub import AudioSegment
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

def getNumberOfTimes(frasearray):
	arrayNumbers =["um","dois","três","quatro","cinco","seis","sete","oito","nove","dez",
				   "onze","doze","treze","catorze","quinze","dezasseis","dezassete","dezoito","dezanove","vinte"]

	if "uma" in fraseArray:
		times = 1
	elif "duas" in fraseArray:
		times = 2
	else:
		for i in frasearray:
			if i.isnumeric():
				times = int(i)
			else:
				if i in arrayNumbers:
					translator = Translator()
					result = translator.translate(i, src='pt', dest='en')
					num = str(result.text)
					times = w2n.word_to_num(num)

	return times


def makeTimesSequence(times,instruction):

	sequence = ""
	for i in range(times):
		sequence += instruction

	return sequence


def getInstructionsByOrder(frasearray):
	sequencia = ""
	for i in fraseArray:

		if i == "cima" or i == "frente":
			sequencia += "C_"

		elif i == "baixo" or i == "tras":
			sequencia += "B_"

		elif i == "direita":
			sequencia += "D_"

		elif i == "esquerda":
			sequencia += "E_"

	return sequencia

fraseReconhecida = ""
sequenciaTraduzida = ""
ciclo = 0
instrucao = ""
fraseArray = ""
times = 0
while (True):

	if "terminar" in fraseArray and "ciclo" in fraseArray:
		ciclo = 0
		audio = AudioSegment.from_file("cicloTerminado.mp3")
		play(audio)
		sequenciaTraduzida += "LE_"

		audio = AudioSegment.from_file("agora.mp3")
		play(audio)

	elif ciclo == 1:
		audio = AudioSegment.from_file("cicloDirecoes.mp3")
		play(audio)
		ciclo = 2

	elif ciclo == 2:

		audio = AudioSegment.from_file("cicloDirecoes.mp3")
		play(audio)
		ciclo = 2

	elif "ciclo" in fraseArray:
		audio = AudioSegment.from_file("ciclo.mp3")
		play(audio)
		ciclo = 1

	elif ("frente" in fraseArray or "cima" in fraseArray )and times == 0:
		audio = AudioSegment.from_file("vezes frente.mp3")
		play(audio)
		instrucao = "C_"
		times = 1

	elif ("baixo" in fraseArray or "tras" in fraseArray)\
		  and times == 0:
		audio = AudioSegment.from_file("vezes baixo.mp3")
		play(audio)
		instrucao = "B_"
		times = 1

	elif "direita" in fraseArray and times == 0:
		audio = AudioSegment.from_file("vezes direita.mp3")
		play(audio)
		instrucao = "D_"
		times = 1

	elif "esquerda" in fraseArray and times == 0:
		audio = AudioSegment.from_file("vezes esquerda.mp3")
		play(audio)
		instrucao = "E_"
		times = 1

	elif "terminar" in fraseArray or "parar" in fraseArray  :
		audio = AudioSegment.from_file("terminarSequencia.mp3")
		play(audio)
		sequenciaTraduzida += "F"
		break

	else:
		audio = AudioSegment.from_file("agora.mp3")
		play(audio)
		times = 0


	rec = sr.Recognizer()
	rec.dynamic_energy_threshold = False

	print("speak ......")
	with sr.Microphone() as fala:
		frase = rec.listen(fala,timeout=60)


	fraseReconhecida = rec.recognize_google(frase, language='pt')
	#translatedFrase = instructionsTranslate(fraseReconhecida.lower())
	print(fraseReconhecida.lower())
	print("\n")
	fraseArray = fraseReconhecida.split(" ")
	if times == 1:
		sequenciaTraduzida += makeTimesSequence( getNumberOfTimes(fraseArray), instrucao)

	if ciclo == 1:
		sequenciaTraduzida +="LB_"
		num = getNumberOfTimes(fraseArray)
		sequenciaTraduzida+= str(num)+"_"

	if ciclo == 2:
		sequenciaTraduzida+=getInstructionsByOrder(fraseArray)
		#sequenciaTraduzida += "LE_"


cred = credentials.Certificate("/Users/goncalocardoso/PycharmProjects/Tese/tese-3ed7d-firebase-adminsdk-4xm5b-3176ad9835.json");
# Initialize the app with a service account, granting admin privileges
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://tese-3ed7d.firebaseio.com/'
});

ref = db.reference('/');
ref.child("sequencia").set(sequenciaTraduzida)

print("sequencia traduzida = "+sequenciaTraduzida)
print("\n")
