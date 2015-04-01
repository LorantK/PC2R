
#include <SFML/Graphics.hpp>
#include <sfeMovie/Movie.hpp>
#include <iostream>

#ifndef MOVIE_FILE
#define MOVIE_FILE "some_movie.ogv"
#endif

/*
 * Here is a little use sample for sfeMovie.
 * It'll open and display the movie specified by MOVIE_FILE above.
 *
 * This sample implements basic controls as follow:
 *  - Escape key to exit
 *  - Space key to play/pause the movie playback
 *  - S key to stop and go back to the beginning of the movie
 *  - R key to restart playing from the beginning of the movie
 *  - F key to toggle between windowed and fullscreen mode
 */

int main (int argc, char ** argv)
{
	if (argc < 4) return 1;

	// Some settings
	const std::string windowTitle = "sfeMovie Player";
	const int windowWidth = atoi(argv[2]);
	const int windowHeight = atoi(argv[3]);
	bool fullscreen = false;
	
	std::cout << "Going to open movie file \"" << MOVIE_FILE << "\"" << std::endl;
	
	// Create window
	sf::RenderWindow window(sf::VideoMode(windowWidth, windowHeight), windowTitle, sf::Style::Close);
	
	// Create and open movie
	sfe::Movie movie;
	if (!movie.openFromFile(argv[1]))
		return 1;

	// Scale movie to the window drawing area and enable VSync
	movie.resizeToFrame(0, 0, window.getSize().x, window.getSize().y);
	window.setVerticalSyncEnabled(true);

	// Start movie playback
	movie.play();

	while (window.isOpen())
	{
		sf::Event ev;
		while (window.pollEvent(ev))
		{
			// Window closure
			if (ev.type == sf::Event::Closed ||
				(ev.type == sf::Event::KeyPressed &&
				 ev.key.code == sf::Keyboard::Escape))
			{
				window.close();
			}
			
			// Handle basic controls
			else if (ev.type == sf::Event::KeyPressed)
			{
				// Play/Pause
				if (ev.key.code == sf::Keyboard::Space)
				{
					if (movie.getStatus() != sfe::Movie::Playing)
						movie.play();
					else
						movie.pause();
				}
				
				// Stop
				if (ev.key.code == sf::Keyboard::S)
					movie.stop();
				
				// Restart playback
				if (ev.key.code == sf::Keyboard::R)
				{
					movie.stop();
					movie.play();
				}
				
				// Toggle fullscreen mode
				if (ev.key.code == sf::Keyboard::F)
				{
					fullscreen = !fullscreen;
					
					// We want to switch to the full screen mode
					if (fullscreen)
					{
						window.create(sf::VideoMode::getDesktopMode(), windowTitle, sf::Style::Fullscreen);
						window.setVerticalSyncEnabled(true);
						movie.resizeToFrame(0, 0, window.getSize().x, window.getSize().y);
					}
					
					// We want to switch back to the windowed mode
					else
					{
						window.create(sf::VideoMode(windowWidth, windowHeight), windowTitle, sf::Style::Close);
						window.setVerticalSyncEnabled(true);
						movie.resizeToFrame(0, 0, window.getSize().x, window.getSize().y);
					}
				}
			}
		}
		
		// Render movie
		window.clear();
		window.draw(movie);
		window.display();
	}

	return 0;
}

