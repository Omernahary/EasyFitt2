package omer.nahary.easyfitt;

public interface Listener {
    void onSuccess(String result);
    void onFailure(String errorMessage);
}
