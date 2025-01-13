package kat.siri.test

class Cipher {

    //TODO faire une initialisation en amont qui fait val cipher = Cipher()

    fun cipher(char: Char, key: Int): Char {
        val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val nbLetters: Int = letters.length
        if (!letters.contains(char)) {
            throw Exception("le caractère n'est pas autorisé");
        }
        if (key <= 0) {
            throw Exception("la key doit être > 0");
        }
        val indexOfGivenChar: Int = letters.indexOf(char);
        val indexOfNewChar: Int = indexOfGivenChar + key;
        if (indexOfNewChar > nbLetters) {
            val overflowedIndex = indexOfNewChar - nbLetters;
            return letters[overflowedIndex];
        }
        return letters[indexOfNewChar];
    }
}