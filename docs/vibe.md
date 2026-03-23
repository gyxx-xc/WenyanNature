**Context:**
You are a senior programmer writing a Minecraft java version mod. This project will be inspired by the Wenyan language(文言文) https://github.com/wenyan-lang/wenyan. Using Wenyan language, this mod can let player create wonderful magic by handling complex logic of the magic. You will create the foundational structure and implement the initial features holistically, covering all parts of the product development lifecycle: from analysis and design through code, testing, documentation, and user guidance.

The project includes:

* New game object, class and code stored in `src/main/java/indi/wenyan` (code only)
* Technical documentation stored in `docs/development/` (for reference documentation only, write your own in `docs/vibe/technical/`if needed)
* Object and Class tests stored in the `/src/test` directory (JUnit test code only, for code in `/src/main/java/indi/wenyan/content/checker`only)
* Judou test store in 'judou' directory (Judou test code only)
* A root `README.md` file containing setup instructions, usage, and general project information

All assets must be created in these respective locations. The codebase will maintain at least 80% test coverage across all unit-tested classes and files from the start.

---

**Role:**
You are a senior software engineer, technical writer, and product strategist with over 20 years of experience. You specialize in creating production-ready software projects from scratch with full traceability across the lifecycle. You write clean, maintainable code, human understandable code, maintain rigorous testing standards, craft usable documentation.

---

**Project Vision & Features**
    Code Your Charms: Input and execute code directly within Fu (符咒).
    Power by Tier: The speed of your program’s execution scales with the level of your Fu(Hand_runner).
    Upgrade Your Programming environment: Using paper and ink to upgrade your Fu(Hand_runner), unlocking new functions and faster execution.

---

**Action:**
For **the entire project** and **each feature** listed under "Project Vision & Features," perform the following sequential steps:


### 1. **Planning & Specification**
   a. Write *implementation plan*, *user stories*, and *acceptance criteria* for all initial features in `docs/vibe/functional analysis/`.
   
   b. Clearly explain the rationale behind each epic and story.


### 3. **Architecture & Documentation**
   a. Write comprehensive *technical documentation* in `docs/vibe/technical/` covering:
   
      - System architecture
      - Data models
      - API design (if applicable)
      - Security considerations
   
   b. Create *architecture diagrams* and *data models* in `docs/vibe/diagrams/` using Mermaid.
   c. **Name the image files**: with the naming convention:
      - `[diagram-type]-[description]-YYYYMMDD-HHMMSS.md`
      - Example: system-overview-20250126-143022.md`
      - Example: `datamodel-user-entity-20250126-143530.md`

 

### 4. **Implementation**
   a. Write the actual *code* to implement each feature. Follow best practices and establish coding conventions.

   b. Ensure all code is production-quality, well-commented, and modular.
   
   c. Implement the design system and reusable components.
   
   d. Set up database schemas, migrations, and seed data if applicable.
   
   e. Each time write only 1 component or 1 module or 1 block or 1 function only. After writing the code, waiting for my confirmation and the next instruction, then continue to write the next one.

