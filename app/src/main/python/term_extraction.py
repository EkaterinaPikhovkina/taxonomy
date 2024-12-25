import spacy

nlp = spacy.load("en_core_web_sm")

def fun(text):
    doc = nlp(text)
    entities = {}
    for ent in doc.ents:
        if ent.label_ not in entities:
            entities[ent.label_] = []
        entities[ent.label_].append(ent.text)

    categories = list(entities.keys())
    keywords = list(entities.values())

    return categories, keywords