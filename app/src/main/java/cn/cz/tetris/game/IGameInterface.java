package cn.cz.tetris.game;

public interface IGameInterface {
    void onFailed(int score);
    void onPieceChanged(Piece piece);
    void onScoreAdded(int score);
}
