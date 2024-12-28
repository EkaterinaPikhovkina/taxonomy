import spacy

nlp_en = spacy.load("en_core_web_sm")

def extract_entities(text):
    doc = nlp_en(text)
    entities = {}
    for ent in doc.ents:
        if ent.label_ not in entities:
            entities[ent.label_] = set()
        entities[ent.label_].add(ent.text)

    categories = list(entities.keys())
    keywords = [list(keyword_set) for keyword_set in entities.values()]

    return categories, keywords
