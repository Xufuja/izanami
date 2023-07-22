import pkg from './package.json' assert { type: "json" };;

export default [
	{
		input: 'src/main.mjs',
		output: {
			name: 'mmd-script-core',
			file: pkg.module,
			format: 'esm'
		}
	}
];
