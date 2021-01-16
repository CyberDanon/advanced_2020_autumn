package entity;

public class ShingleBuilder {
    private ShingleDTO shingleDTO;
    public ShingleBuilder(){
        shingleDTO = new ShingleDTO();
    }
    public ShingleBuilder setDocumentId(int documentId){
        shingleDTO.setDocumentId(documentId);
        return this;
    }
    public ShingleBuilder setShingleText(int text){
        shingleDTO.setShingleText(text);
        return this;
    }
    public ShingleBuilder setShingleSize(int size){
        shingleDTO.setShingleMaxValue(size);
        return this;
    }
    public ShingleDTO get(){
        return shingleDTO;
    }
}
