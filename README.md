<p><img src="./title.png" alt="Logo" width="720"></p>

<h1>Wenyan Nature  <br>
    <br>
</h1>

## Overview

Wenyan Nature is a Minecraft mod that introduces new entities, items, and blocks inspired by the Wenyan language and culture. This mod allows players to interact with new mechanics and features, enhancing their gameplay experience.

## Features

- **Hand Runner Entity**: A unique entity that can be summoned by players and executes Wenyan code.
- **Bullet Entity**: A projectile entity with custom rendering.
- **Runner Block**: A block that interacts with the Hand Runner entity.
- **Custom Renderers**: Special renderers for the new entities and blocks.

## Installation

1. **Clone the Repository**:
    ```sh
    git clone https://github.com/gyxx-xc/wenyan-nature.git
    cd wenyan-nature
    ```

2. **Set up the Development Environment**:
   Ensure you have Java 21 and Gradle installed. Then, run:
    ```sh
    ./gradlew setupDecompWorkspace
    ./gradlew eclipse
    ```

3. **Build the Mod**:
    ```sh
    ./gradlew build
    ```

4. **Add to Minecraft**:
   Copy the generated `.jar` file from `build/libs` to your Minecraft `mods` folder.

## Usage

### Hand Runner

- **Crafting**: Craft the Hand Runner item using the provided recipe.
- **Using the Hand Runner**: Right-click to open the Hand Runner screen and input Wenyan code.
- **Dropping the Hand Runner**: Drop the item to summon the Hand Runner entity, which will execute the code.

### Runner Block

- **Placing the Block**: Place the Runner Block in the world.
- **Interacting**: Interact with the block to link it with the Hand Runner entity.

## Development

### Prerequisites

- Java 21
- Gradle
- IntelliJ IDEA or Eclipse

### Building from Source

1. **Clone the Repository**:
    ```sh
    git clone https://github.com/gyxx-xc/wenyan-nature.git
    cd wenyan-nature
    ```

2. **Import the Project**:
   Open the project in your preferred IDE (IntelliJ IDEA or Eclipse).

3. **Run the Client**:
   Use the Gradle task to run the Minecraft client with the mod:
    ```sh
    ./gradlew runClient
    ```

## Contributing

1. **Fork the Repository**.
2. **Create a Feature Branch**:
    ```sh
    git checkout -b feature/your-feature
    ```
3. **Commit Your Changes**:
    ```sh
    git commit -m "Add your feature"
    ```
4. **Push to the Branch**:
    ```sh
    git push origin feature/your-feature
    ```
5. **Open a Pull Request**.

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.

## Acknowledgements

- Thanks to the Minecraft Forge community for their support and resources.
- Special thanks to the Wenyan language community for their inspiration.

## Contact

For any inquiries or support, please open an issue on the GitHub repository.